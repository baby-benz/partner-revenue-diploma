package ru.itmo.eventprocessor.service.so;

import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FullEventSO(String eventId,
                          BigDecimal amount,
                          OffsetDateTime timestamp,
                          String profileId,
                          String pointId,
                          Status status) {
}
