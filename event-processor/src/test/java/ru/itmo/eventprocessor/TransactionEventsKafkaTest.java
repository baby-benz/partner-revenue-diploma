package ru.itmo.eventprocessor;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import ru.itmo.eventprocessor.domain.message.TransactionEventOuterClass.TransactionEvent;
import ru.itmo.eventprocessor.kafka.consumer.TransactionConsumer;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}"})
public class TransactionEventsKafkaTest {
    @Autowired
    private KafkaTemplate<String, byte[]> template;
    @Autowired
    private TransactionConsumer consumer;

    @Value("${kafka.topics.transaction}")
    private String topic;

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingSimpleEvent_thenEventReceived() throws Exception {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        TransactionEvent message = TransactionEvent.newBuilder()
                .setId("1")
                .setAmount(300)
                .setTimestamp(timestamp)
                .setPointId("2")
                .setProfileId("3")
                .build();

        template.send(topic, message.toByteArray());

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertEquals(message, consumer.getEventPayload());
    }
}
