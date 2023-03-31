package ru.itmo.eventprocessor.web.dto.response;

import ru.itmo.eventprocessor.domain.enumeration.Status;

public record FullEventResponse(String eventId,
                                double amount,
                                String zonedTimestamp,
                                String profileId,
                                String pointId,
                                Status status) {
}
