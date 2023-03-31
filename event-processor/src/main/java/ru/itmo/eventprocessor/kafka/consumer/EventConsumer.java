package ru.itmo.eventprocessor.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.eventprocessor.domain.message.Event.EventMessage;
import ru.itmo.eventprocessor.service.EventService;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
@Component
public class EventConsumer {
    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private EventMessage eventPayload;
    private final EventService eventService;

    @KafkaListener(topics = KafkaTopics.EVENT)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        eventPayload = EventMessage.parseFrom(message);
        eventService.process(eventPayload);
        latch.countDown();
    }
}
