package ru.itmo.eventprocessor.service.so;

import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.time.OffsetDateTime;

public record FullEventSO(String eventId,
                          double amount,
                          OffsetDateTime timestamp,
                          String profileId,
                          String pointId,
                          Status status) {
}
