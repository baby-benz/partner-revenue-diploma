package ru.itmo.eventprocessor.service.so.in;

import ru.itmo.common.domain.message.EventStatus;
import ru.itmo.eventprocessor.domain.enumeration.Status;

public record UpdateEventStatusSO(String id, Status status) {
    public static UpdateEventStatusSO fromEventStatusMessage(EventStatus.EventStatusMessage eventStatusMessage) {
        return new UpdateEventStatusSO(
                eventStatusMessage.getEventId(),
                Status.valueOf(eventStatusMessage.getEventStatus().name())
        );
    }
}
