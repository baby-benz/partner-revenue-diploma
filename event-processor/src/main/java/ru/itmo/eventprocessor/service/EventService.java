package ru.itmo.eventprocessor.service;

import ru.itmo.eventprocessor.service.so.FullEventSO;
import ru.itmo.eventprocessor.service.so.in.UpdateEventStatusSO;
import ru.itmo.eventprocessor.service.so.in.PaymentEventSO;

public interface EventService {
    void process(PaymentEventSO event);
    FullEventSO getEvent(String eventId);
    void updateEventStatus(UpdateEventStatusSO eventStatus);
}
