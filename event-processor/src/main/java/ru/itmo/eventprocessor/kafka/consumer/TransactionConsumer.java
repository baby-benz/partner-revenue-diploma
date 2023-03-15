package ru.itmo.eventprocessor.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.eventprocessor.domain.message.TransactionEventOuterClass.TransactionEvent;

import java.util.concurrent.CountDownLatch;

@Component
@Getter
public class TransactionConsumer {
    private final CountDownLatch latch = new CountDownLatch(1);
    private TransactionEvent eventPayload;

    @KafkaListener(topics = "${kafka.topics.transaction}")
    public void consumeTransaction(byte[] message) throws InvalidProtocolBufferException {
        eventPayload = TransactionEvent.parseFrom(message);
        latch.countDown();
    }
}
