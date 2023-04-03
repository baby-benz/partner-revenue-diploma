package ru.itmo.eventprocessor.web.dto.response;

import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.math.BigDecimal;

public record FullEventResponse(String eventId,
                                BigDecimal amount,
                                String zonedTimestamp,
                                String profileId,
                                String pointId,
                                Status status) {
}
