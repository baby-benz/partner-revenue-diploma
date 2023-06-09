package ru.itmo.eventprocessor;

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
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.domain.message.PaymentEvent;
import ru.itmo.common.domain.message.ProtoBigDecimal;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.eventprocessor.domain.enumeration.Status;
import ru.itmo.eventprocessor.kafka.consumer.PaymentEventConsumer;
import ru.itmo.eventprocessor.kafka.producer.CalcInfoProducer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}"})
@AutoConfigureMockMvc
class EventLifecycleTest {
    @Autowired
    KafkaTemplate<String, byte[]> template;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PaymentEventConsumer consumer;
    @Autowired
    CalcInfoProducer producer;
    @MockBean
    ProfilePointClient profilePointClient;

    @Value("${spring.kafka.consumer.group-id")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Consumer<String, byte[]> calcInfoConsumer;

    @BeforeEach
    void init() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        calcInfoConsumer = new KafkaConsumer<>(props);
        calcInfoConsumer.subscribe(Collections.singletonList(KafkaTopics.PAYMENT_EVENT));
    }

    @Test
    void when_eventReceived_then_controllerReturnsEvent_and_eventSent() throws Exception {
        final String eventId = UUID.randomUUID().toString();
        final BigDecimal amount = BigDecimal.valueOf(300.d);
        final OffsetDateTime timestamp = OffsetDateTime.now(ZoneId.systemDefault());
        final String profileId = UUID.randomUUID().toString();
        final String pointId = UUID.randomUUID().toString();

        final ProtoBigDecimal.DecimalValue amountProto = BigDecimalUtil.toProtoDecimalValue(amount);

        final PaymentEvent.PaymentEventMessage message = PaymentEvent.PaymentEventMessage.newBuilder()
                .setId(eventId)
                .setAmount(amountProto)
                .setTimestamp(timestamp.toString())
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        doReturn(true).when(profilePointClient).pointAndProfileMatches(Mockito.anyString(), Mockito.anyString());

        template.send(KafkaTopics.PAYMENT_EVENT, message.toByteArray());

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        boolean messagePublished = producer.getLatch().await(10, TimeUnit.SECONDS);

        String expectedTimestampString = timestamp.atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        assertTrue(messageConsumed);
        assertTrue(messagePublished);
        assertEquals(message, consumer.getEventPayload());
        assertEquals(1L, producer.getMessagePublished());

        mockMvc.perform(get(Endpoint.EventProcessor.GET_FULL, eventId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(eventId),
                jsonPath("$.amount").value(amount),
                jsonPath("$.zonedTimestamp").value(
                        CoreMatchers.startsWith(expectedTimestampString.substring(0, 21))
                ),
                jsonPath("$.zonedTimestamp").value(
                        CoreMatchers.endsWith(expectedTimestampString.substring(33))
                ),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.status").value(Status.NOT_PROCESSED.name())
        );

        var expectedCalcInfo = CalcInfo.CalcInfoMessage.newBuilder()
                .setEventId(eventId)
                .setAmount(amountProto)
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        calcInfoConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                assertEquals(expectedCalcInfo, CalcInfo.CalcInfoMessage.parseFrom(record.value()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });
    }
}
