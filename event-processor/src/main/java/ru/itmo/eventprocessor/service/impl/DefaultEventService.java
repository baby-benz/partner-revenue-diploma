package ru.itmo.eventprocessor.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.eventprocessor.domain.entity.Event;
import ru.itmo.eventprocessor.domain.enumeration.Status;
import ru.itmo.eventprocessor.kafka.producer.CalcInfoProducer;
import ru.itmo.eventprocessor.repository.EventRepository;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.FullEventSO;
import ru.itmo.eventprocessor.service.so.in.UpdateEventStatusSO;
import ru.itmo.eventprocessor.service.so.in.PaymentEventSO;
import ru.itmo.common.service.util.BigDecimalUtil;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultEventService implements EventService {
    private final EventRepository eventRepository;
    private final ProfilePointClient profilePointClient;
    private final CalcInfoProducer calcInfoProducer;

    @Override
    public void process(PaymentEventSO event) {
        boolean matching = profilePointClient.pointAndProfileMatches(event.pointId(), event.profileId());

        eventRepository.save(
                new Event(
                        UUID.fromString(event.id()),
                        event.amount(),
                        event.timestamp(),
                        UUID.fromString(event.profileId()),
                        UUID.fromString(event.pointId()),
                        matching ? Status.NOT_PROCESSED : Status.UNPROCESSABLE
                )
        );

        calcInfoProducer.sendMessage(
                CalcInfo.CalcInfoMessage.newBuilder()
                        .setEventId(event.id())
                        .setAmount(BigDecimalUtil.toProtoDecimalValue(event.amount()))
                        .setProfileId(event.profileId())
                        .setPointId(event.pointId())
                        .build()
        );
    }

    @Override
    public FullEventSO getEvent(String eventId) {
        UUID id = UUID.fromString(eventId);

        if (!eventRepository.existsById(id)) {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.EVENT_NOT_FOUND)),
                    eventId
            );
        }

        Event event = eventRepository.getReferenceById(id);

        return new FullEventSO(
                eventId,
                event.getAmount(),
                event.getEventTime(),
                event.getProfileId().toString(),
                event.getPointId().toString(),
                event.getEventStatus()
        );
    }

    @Override
    public void updateEventStatus(UpdateEventStatusSO eventStatus) {
        try {
            Event event = eventRepository.getReferenceById(UUID.fromString(eventStatus.id()));
            event.setEventStatus(eventStatus.status());
            eventRepository.save(event);
        } catch (EntityNotFoundException e) {
            log.error("Status of event with id + [" + eventStatus.id() + "] cannot be updated due to absence in the DB");
        }
    }
}
