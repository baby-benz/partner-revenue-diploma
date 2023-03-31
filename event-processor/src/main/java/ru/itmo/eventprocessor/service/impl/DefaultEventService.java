package ru.itmo.eventprocessor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.PointClient;
import ru.itmo.eventprocessor.domain.entity.Event;
import ru.itmo.eventprocessor.domain.enumeration.Status;
import ru.itmo.eventprocessor.domain.message.CalcInfo.CalcInfoMessage;
import ru.itmo.eventprocessor.domain.message.Event.EventMessage;
import ru.itmo.eventprocessor.kafka.producer.CalcInfoProducer;
import ru.itmo.eventprocessor.repository.EventRepository;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.FullEventSO;

import java.time.*;

@RequiredArgsConstructor
@Service
public class DefaultEventService implements EventService {
    private final EventRepository eventRepository;
    private final PointClient pointClient;
    private final CalcInfoProducer calcInfoProducer;

    @Override
    public void process(EventMessage event) {
        pointClient.checkPointAndProfileMatch(event.getPointId(), event.getProfileId());

        eventRepository.save(
                new Event(
                        event.getId(),
                        event.getAmount(),
                        OffsetDateTime.parse(event.getTimestamp()).withOffsetSameInstant(ZoneOffset.UTC),
                        event.getProfileId(),
                        event.getPointId(),
                        Status.NOT_PROCESSED
                )
        );

        calcInfoProducer.sendMessage(
                CalcInfoMessage.newBuilder()
                        .setId(event.getId())
                        .setAmount(event.getAmount())
                        .setProfileId(event.getProfileId())
                        .setPointId(event.getPointId())
                        .build()
        );
    }

    @Override
    public FullEventSO getEvent(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new HttpStatusCodeException(NotFoundErrorCause.EVENT_NOT_FOUND, eventId);
        }

        Event event = eventRepository.getReferenceById(eventId);

        return new FullEventSO(
                event.getId(),
                event.getAmount(),
                event.getTimestamp(),
                event.getProfileId(),
                event.getPointId(),
                event.getStatus()
        );
    }
}
