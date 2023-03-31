package ru.itmo.eventprocessor.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.eventprocessor.domain.message.EventStatus.EventStatusMessage;

import java.util.concurrent.CountDownLatch;

@Component
@Getter
public class EventStatusConsumer {
    private final CountDownLatch latch = new CountDownLatch(1);
    private EventStatusMessage eventPayload;

    @KafkaListener(topics = KafkaTopics.EVENT_STATUS)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        eventPayload = EventStatusMessage.parseFrom(message);
        latch.countDown();
    }
}