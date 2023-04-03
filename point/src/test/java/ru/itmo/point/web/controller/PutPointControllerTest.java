package ru.itmo.point.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.domain.enumeration.PointType;
import ru.itmo.point.web.dto.request.UpdatePointRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class PutPointControllerTest extends PointControllerTest {
    final String profileIdChangeTo = "newId";
    final String pointNameChangeTo = "newName";
    final PointType pointTypeChangeTo = PointType.PRODUCT_SELLING;
    final Status statusChangeTo = Status.ACTIVE;

    @Test
    void when_createPoint_and_putPoint_then_ok() throws Exception {
        final String pointId = createSamplePoint();

        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );
        final String jsonUpdatePointRequestObject = objectMapper.writeValueAsString(updatePointRequest);

        mockMvc.perform(put(Endpoint.Point.PUT, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonUpdatePointRequestObject)
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.profileId").value(profileIdChangeTo),
                jsonPath("$.name").value(pointNameChangeTo),
                jsonPath("$.pointType").value(pointTypeChangeTo.name()),
                jsonPath("$.status").value(statusChangeTo.name())
        );
    }

    @Test
    void when_putPoint_with_wrongPointId_then_notFoundResponse() throws Exception {
        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );

        final String pointId = "wrongId";
        final String expectedMessageCode = NotFoundErrorCause.POINT_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{pointId},
                Locale.ENGLISH
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
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
        final var updatePointRequest = new UpdatePointRequest(
                profileIdChangeTo,
                pointNameChangeTo,
                pointTypeChangeTo,
                statusChangeTo,
                null
        );

        final String pointId = "wrongId";
        final String expectedMessageCode = NotFoundErrorCause.POINT_NOT_FOUND.getMessageCode();
        final var russianLocale = new Locale("ru", "RU");
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{pointId},
                russianLocale
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
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
