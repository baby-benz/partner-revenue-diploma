package ru.itmo.rewardcalculator.web.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.domain.message.CalculatedReward;
import ru.itmo.common.domain.message.ProtoBigDecimal;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.common.web.dto.response.calcscheme.CalcRule;
import ru.itmo.common.web.dto.response.calcscheme.CalcScheme;
import ru.itmo.rewardcalculator.kafka.consumer.CalcInfoConsumer;
import ru.itmo.rewardcalculator.kafka.producer.CalculatedRewardProducer;

import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RewardFullCycleTest {
    @Autowired
    KafkaTemplate<String, byte[]> template;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    CalcInfoConsumer consumer;
    @Autowired
    CalculatedRewardProducer producer;
    @MockBean
    ProfilePointClient profilePointClient;
    @MockBean
    CalcSchemeClient calcSchemeClient;

    @Value("${spring.kafka.consumer.group-id")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Consumer<String, byte[]> rewardConsumer;

    @BeforeEach
    void init() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        rewardConsumer = new KafkaConsumer<>(props);
        rewardConsumer.subscribe(Collections.singletonList(KafkaTopics.CALCULATED_REWARD));
        log.info("""
                        Test data:
                                                        
                        Event1 ID:      {}
                        Event2 ID:      {}
                        Profile ID:     {}
                        Point ID:       {}
                        CalcScheme ID:  {}""",
                event1Id, event2Id, profileId, pointId, calcSchemeId);
    }

    final String event1Id = UUID.randomUUID().toString();
    final String event2Id = UUID.randomUUID().toString();
    final BigDecimal amount1 = BigDecimal.valueOf(50.d);
    final BigDecimal amount2 = BigDecimal.valueOf(250.d);
    final String profileId = UUID.randomUUID().toString();
    final String pointId = UUID.randomUUID().toString();
    final String calcSchemeId = UUID.randomUUID().toString();
    final CalcScheme calcScheme = new CalcScheme(
            calcSchemeId,
            List.of(new CalcRule(UUID.randomUUID().toString(), 0L, 0.05f, 0),
                    new CalcRule(UUID.randomUUID().toString(), 80L, 0.07f, 11),
                    new CalcRule(UUID.randomUUID().toString(), 145L, 0.09f, 46)),
            false
    );

    @Test
    void when_calcInfoReceived_then_controllerReturnsReward_and_calculatedRewardSent() throws Exception {
        final ProtoBigDecimal.DecimalValue amount1Proto = BigDecimalUtil.toProtoDecimalValue(amount1);
        final ProtoBigDecimal.DecimalValue amount2Proto = BigDecimalUtil.toProtoDecimalValue(amount2);

        final CalcInfo.CalcInfoMessage message1 = CalcInfo.CalcInfoMessage.newBuilder()
                .setEventId(event1Id)
                .setAmount(amount1Proto)
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        doReturn(calcScheme.id()).when(profilePointClient).getCalcSchemeId(anyString());
        doReturn(calcScheme).when(calcSchemeClient).getCalcScheme(calcScheme.id());

        template.send(KafkaTopics.CALC_INFO, message1.toByteArray());

        consumer.setLatch(new CountDownLatch(1));
        producer.setLatch(new CountDownLatch(1));
        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        assertTrue(producer.getLatch().await(10, TimeUnit.SECONDS));
        assertEquals(message1, consumer.getPayload());
        assertEquals(1L, producer.getMessagePublished());

        final BigDecimal expectedReward1 = BigDecimal.valueOf(2.5d);

        rewardConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                CalculatedReward.RewardMessage message = CalculatedReward.RewardMessage.parseFrom(record.value());
                assertEquals(event1Id, message.getEventId());
                assertEquals(pointId, message.getPointId());
                assertEquals(amount1Proto, message.getEventAmount());
                assertEquals(
                        expectedReward1,
                        BigDecimalUtil.toJavaBigDecimal(message.getRewardAmount()).setScale(1, RoundingMode.HALF_EVEN)
                );
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });

        final CalcInfo.CalcInfoMessage message2 = CalcInfo.CalcInfoMessage.newBuilder()
                .setEventId(event2Id)
                .setAmount(amount2Proto)
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        template.send(KafkaTopics.CALC_INFO, message2.toByteArray());

        consumer.setLatch(new CountDownLatch(1));
        producer.setLatch(new CountDownLatch(1));
        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        assertTrue(producer.getLatch().await(10, TimeUnit.SECONDS));
        assertEquals(message2, consumer.getPayload());
        assertEquals(2L, producer.getMessagePublished());

        final BigDecimal expectedReward2 = BigDecimal.valueOf(77d);

        rewardConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                CalculatedReward.RewardMessage message = CalculatedReward.RewardMessage.parseFrom(record.value());
                assertEquals(event2Id, message.getEventId());
                assertEquals(pointId, message.getPointId());
                assertEquals(amount2Proto, message.getEventAmount());
                assertEquals(
                        expectedReward2,
                        BigDecimalUtil.toJavaBigDecimal(message.getRewardAmount()).setScale(1, RoundingMode.HALF_EVEN)
                );
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(get(Endpoint.RewardCalculator.GET_FULL, event1Id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(event1Id),
                jsonPath("$.eventAmount").value(amount1),
                result -> assertEquals(expectedReward1, BigDecimal.valueOf(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.rewardAmount")
                        ).setScale(1, RoundingMode.HALF_EVEN)
                ),
                result -> assertDoesNotThrow(() ->
                        ZonedDateTime.parse(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.timestamp")
                        )
                ),
                jsonPath("$.formula").value("(50.00 * 0.05 + 0)"),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.calcSchemeId").value(calcSchemeId)
        );

        mockMvc.perform(get(Endpoint.RewardCalculator.GET_FULL, event2Id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(event2Id),
                jsonPath("$.eventAmount").value(amount2),
                jsonPath("$.rewardAmount").value(expectedReward2),
                result -> assertDoesNotThrow(() ->
                        ZonedDateTime.parse(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.timestamp")
                        )
                ),
                jsonPath("$.formula").value("(30.00 * 0.05 + 0) + (65.00 * 0.07 + 11) + (155.00 * 0.09 + 46)"),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.calcSchemeId").value(calcSchemeId)
        );
    }

    @Test
    void when_calcInfoWithRecalcReceived_then_controllerReturnsReward_and_calculatedRewardSent() throws Exception {
        final CalcInfo.CalcInfoMessage message1 = CalcInfo.CalcInfoMessage.newBuilder()
                .setEventId(event1Id)
                .setAmount(BigDecimalUtil.toProtoDecimalValue(amount1))
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        final CalcScheme calcScheme = new CalcScheme(
                calcSchemeId,
                List.of(new CalcRule(UUID.randomUUID().toString(), 0L, 0.05f, 0),
                        new CalcRule(UUID.randomUUID().toString(), 80L, 0.07f, 11),
                        new CalcRule(UUID.randomUUID().toString(), 145L, 0.09f, 46)),
                true
        );

        doReturn(calcScheme.id()).when(profilePointClient).getCalcSchemeId(anyString());
        doReturn(calcScheme).when(calcSchemeClient).getCalcScheme(calcScheme.id());

        template.send(KafkaTopics.CALC_INFO, message1.toByteArray());

        consumer.setLatch(new CountDownLatch(1));
        producer.setLatch(new CountDownLatch(1));
        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        assertTrue(producer.getLatch().await(10, TimeUnit.SECONDS));
        assertEquals(message1, consumer.getPayload());
        assertEquals(1L, producer.getMessagePublished());

        final BigDecimal expectedReward1 = BigDecimal.valueOf(2.5d);

        rewardConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                CalculatedReward.RewardMessage message = CalculatedReward.RewardMessage.parseFrom(record.value());
                assertEquals(event1Id, message.getEventId());
                assertEquals(pointId, message.getPointId());
                assertEquals(amount1, BigDecimalUtil.toJavaBigDecimal(message.getEventAmount()).setScale(1, RoundingMode.HALF_EVEN));
                assertEquals(
                        expectedReward1,
                        BigDecimalUtil.toJavaBigDecimal(message.getRewardAmount()).setScale(1, RoundingMode.HALF_EVEN)
                );
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });

        final CalcInfo.CalcInfoMessage message2 = CalcInfo.CalcInfoMessage.newBuilder()
                .setEventId(event2Id)
                .setAmount(BigDecimalUtil.toProtoDecimalValue(amount2))
                .setProfileId(profileId)
                .setPointId(pointId)
                .build();

        template.send(KafkaTopics.CALC_INFO, message2.toByteArray());

        consumer.setLatch(new CountDownLatch(1));
        producer.setLatch(new CountDownLatch(1));
        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        assertTrue(producer.getLatch().await(10, TimeUnit.SECONDS));
        assertEquals(message2, consumer.getPayload());
        assertEquals(3L, producer.getMessagePublished());

        final BigDecimal expectedReward2 = BigDecimal.valueOf(4.5d);
        final BigDecimal expectedReward3 = BigDecimal.valueOf(68.5d);

        rewardConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                CalculatedReward.RewardMessage message = CalculatedReward.RewardMessage.parseFrom(record.value());
                assertEquals(event1Id, message.getEventId());
                assertEquals(pointId, message.getPointId());
                assertEquals(amount1, BigDecimalUtil.toJavaBigDecimal(message.getEventAmount()).setScale(1, RoundingMode.HALF_EVEN));
                assertEquals(
                        expectedReward2,
                        BigDecimalUtil.toJavaBigDecimal(message.getRewardAmount()).setScale(1, RoundingMode.HALF_EVEN)
                );
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });

        rewardConsumer.poll(Duration.ofMillis(300)).forEach(record -> {
            try {
                CalculatedReward.RewardMessage message = CalculatedReward.RewardMessage.parseFrom(record.value());
                assertEquals(event2Id, message.getEventId());
                assertEquals(pointId, message.getPointId());
                assertEquals(amount2, BigDecimalUtil.toJavaBigDecimal(message.getEventAmount()).setScale(1, RoundingMode.HALF_EVEN));
                assertEquals(
                        expectedReward3,
                        BigDecimalUtil.toJavaBigDecimal(message.getRewardAmount()).setScale(1, RoundingMode.HALF_EVEN)
                );
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(get(Endpoint.RewardCalculator.GET_FULL, event1Id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(event1Id),
                jsonPath("$.eventAmount").value(amount1),
                result -> assertEquals(expectedReward2, BigDecimal.valueOf(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.rewardAmount")
                        ).setScale(1, RoundingMode.HALF_EVEN)
                ),
                result -> assertDoesNotThrow(() ->
                        ZonedDateTime.parse(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.timestamp")
                        )
                ),
                jsonPath("$.formula").value("(50.00 * 0.09 + 0)"),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.calcSchemeId").value(calcSchemeId)
        );

        mockMvc.perform(get(Endpoint.RewardCalculator.GET_FULL, event2Id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.eventId").value(event2Id),
                jsonPath("$.eventAmount").value(amount2),
                jsonPath("$.rewardAmount").value(expectedReward3),
                result -> assertDoesNotThrow(() ->
                        ZonedDateTime.parse(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.timestamp")
                        )
                ),
                jsonPath("$.formula").value("(250.00 * 0.09 + 46)"),
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.calcSchemeId").value(calcSchemeId)
        );
    }
}
