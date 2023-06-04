package ru.itmo.profilepointservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.profilepointservice.domain.enumeration.PointType;
import ru.itmo.profilepointservice.web.dto.request.UpdatePointRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class PutPointControllerTest extends PointControllerTest {
    final String pointNameChangeTo = "newName";
    final PointType pointTypeChangeTo = PointType.PRODUCT_SELLING;
    final Status statusChangeTo = Status.ACTIVE;

    @Test
    void when_createPoint_and_putPoint_then_ok() throws Exception {
        final ProfileAndPointId profileAndPointId = createSamplePoint();

        final String profileIdChangeTo = createSampleProfile();

        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );
        final String jsonUpdatePointRequestObject = objectMapper.writeValueAsString(updatePointRequest);

        mockMvc.perform(put(Endpoint.Point.PUT, profileAndPointId.pointId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonUpdatePointRequestObject)
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.pointId").value(profileAndPointId.pointId()),
                jsonPath("$.profileId").value(profileIdChangeTo),
                jsonPath("$.name").value(pointNameChangeTo),
                jsonPath("$.pointType").value(pointTypeChangeTo.name()),
                jsonPath("$.status").value(statusChangeTo.name())
        );
    }

    @Test
    void when_putPoint_with_wrongPointId_then_notFoundResponse() throws Exception {
        final String profileIdChangeTo = createSampleProfile();

        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );

        final String pointId = UUID.randomUUID().toString();
        final String expectedMessageCode = HttpErrorCause.NotFound.POINT_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{pointId},
                Locale.ENGLISH
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                List.of(expectedMessageCode),
                List.of(expectedResponseMessage)
        );
        final String jsonUpdatePointRequestObject = objectMapper.writeValueAsString(updatePointRequest);

        mockMvc.perform(put(Endpoint.Point.PUT, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonUpdatePointRequestObject)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponseObject,
                        objectMapper.readValue(result.getResponse().getContentAsString(), DefaultApiErrorResponse.class)
                )
        );
    }

    @Test
    void when_putPoint_with_wrongPointId_and_ruLangHeader_then_notFoundResponse() throws Exception {
        final String profileIdChangeTo = createSampleProfile();

        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );

        final String pointId = UUID.randomUUID().toString();
        final String expectedMessageCode = HttpErrorCause.NotFound.POINT_NOT_FOUND.getMessageCode();
        final var russianLocale = new Locale("ru", "RU");
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{pointId},
                russianLocale
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                List.of(expectedMessageCode),
                List.of(expectedResponseMessage)
        );
        final String jsonUpdatePointRequestObject = objectMapper.writeValueAsString(updatePointRequest);

        mockMvc.perform(put(Endpoint.Point.PUT, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(russianLocale)
                .content(jsonUpdatePointRequestObject)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponseObject,
                        objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), DefaultApiErrorResponse.class)
                )
        );
    }
}
