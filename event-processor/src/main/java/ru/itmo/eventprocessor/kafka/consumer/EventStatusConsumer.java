package ru.itmo.eventprocessor.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.EventStatus;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.in.UpdateEventStatusSO;

import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class EventStatusConsumer {
    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private EventStatus.EventStatusMessage eventPayload;
    private final EventService eventService;

    @KafkaListener(topics = KafkaTopics.EVENT_STATUS)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        eventPayload = EventStatus.EventStatusMessage.parseFrom(message);
        eventService.updateEventStatus(UpdateEventStatusSO.fromEventStatusMessage(eventPayload));
        latch.countDown();
    }
}