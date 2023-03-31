package ru.itmo.eventprocessor.service;

import ru.itmo.eventprocessor.domain.message.Event.EventMessage;
import ru.itmo.eventprocessor.service.so.FullEventSO;

public interface EventService {
    void process(EventMessage event);
    FullEventSO getEvent(String eventId);
}
