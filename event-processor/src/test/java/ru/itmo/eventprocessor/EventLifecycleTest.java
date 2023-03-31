package ru.itmo.eventprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.web.client.PointClient;
import ru.itmo.eventprocessor.domain.enumeration.Status;
import ru.itmo.eventprocessor.domain.message.CalcInfo.CalcInfoMessage;
import ru.itmo.eventprocessor.domain.message.Event.EventMessage;
import ru.itmo.eventprocessor.kafka.consumer.EventConsumer;
import ru.itmo.eventprocessor.kafka.producer.CalcInfoProducer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}"})
@AutoConfigureMockMvc
class EventLifecycleTest {
    @Autowired
    KafkaTemplate<String, byte[]> template;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EventConsumer consumer;
    @Autowired
    CalcInfoProducer producer;
    ObjectMapper objectMapper;

    @Value("${spring.kafka.consumer.group-id")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @MockBean
    PointClient pointClient;

    Consumer<String, byte[]> calcInfoConsumer;

    @BeforeEach
    void initObjectMapper() {
        objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        calcInfoConsumer = new KafkaConsumer<>(props);
        calcInfoConsumer.subscribe(Collections.singletonList(KafkaTopics.CALC_INFO));
    }

    @Test
    void whenEventReceived_then_controllerReturnsEvent_and_eventSent() throws Exception {
        final String eventId = UUID.randomUUID().toString();
        final double amount = 300.d;
        final OffsetDateTime timestamp = OffsetDateTime.now(ZoneId.systemDefault());
        final String profileId = UUID.randomUUID().toString();
        final String pointId = UUID.randomUUID().toString();
        final Status status = Status.NOT_PROCESSED;

        EventMessage message = EventMessage.newBuilder()
                .setId(eventId)
                .setAmount(amount)
                .setTimestamp(timestamp.toString())
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        doNothing().when(pointClient).checkPointAndProfileMatch(Mockito.anyString(), Mockito.anyString());

        template.send(KafkaTopics.EVENT, message.toByteArray());

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        boolean messagePublished = producer.getLatch().await(10, TimeUnit.SECONDS);

        String expectedTimestampString = timestamp.atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        mockMvc.perform(get(Endpoint.EventProcessor.GET_FULL, eventId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(eventId),
                jsonPath("$.amount").value(amount),
                jsonPath("$.zonedTimestamp").value(CoreMatchers.startsWith(expectedTimestampString.substring(0, 21))),
                jsonPath("$.zonedTimestamp").value(CoreMatchers.endsWith(expectedTimestampString.substring(33))),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.status").value(status.name())
        );
        assertTrue(messageConsumed);
        assertTrue(messagePublished);
        assertEquals(message, consumer.getEventPayload());
        assertEquals(1L, producer.getMessagePublished());

        var expectedCalcInfo = CalcInfoMessage.newBuilder()
                .setId(eventId)
                .setAmount(amount)
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        calcInfoConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                assertEquals(expectedCalcInfo, CalcInfoMessage.parseFrom(record.value()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });
    }
}
