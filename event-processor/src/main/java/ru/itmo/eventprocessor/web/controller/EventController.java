package ru.itmo.eventprocessor.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.service.util.TimestampUtil;
import ru.itmo.eventprocessor.service.EventService;
import ru.itmo.eventprocessor.service.so.FullEventSO;
import ru.itmo.eventprocessor.web.dto.response.FullEventResponse;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = Endpoint.EventProcessor.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullEventResponse getEvent(@PathVariable @NotBlank String eventId) {
        FullEventSO event = eventService.getEvent(eventId);

        return new FullEventResponse(
                event.eventId(),
                event.amount(),
                TimestampUtil.toString(event.timestamp()),
                event.profileId(),
                event.pointId(),
                event.status()
        );
    }
}
