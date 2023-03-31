package ru.itmo.eventprocessor.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.FullEventSO;
import ru.itmo.eventprocessor.web.dto.response.FullEventResponse;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = Endpoint.EventProcessor.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullEventResponse getEvent(@PathVariable String eventId) {
        FullEventSO event = eventService.getEvent(eventId);

        return new FullEventResponse(
                event.eventId(),
                event.amount(),
                event.timestamp().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
                event.profileId(),
                event.pointId(),
                event.status()
        );
    }
}
