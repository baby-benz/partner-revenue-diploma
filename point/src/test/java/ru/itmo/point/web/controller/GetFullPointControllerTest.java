package ru.itmo.point.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.web.dto.response.FullPointResponse;
import ru.itmo.point.web.test.Data;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GetFullPointControllerTest extends PointControllerTest {
    @Test
    void when_getFullPoint_then_ok() throws Exception {
        final String pointId = createSamplePoint();

        final FullPointResponse expectedPointResponse = new FullPointResponse(
                pointId,
                Data.SAMPLE_PROFILE_ID,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        );

        mockMvc.perform(get(Endpoint.Point.GET_FULL, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                response -> assertEquals(
                        expectedPointResponse,
                        objectMapper.readValue(response.getResponse().getContentAsString(), FullPointResponse.class)
                )
        );
    }

    @Test
    void when_getFullPoint_with_wrongId_then_notFound() throws Exception {
        String pointId = "wrongId";

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

        mockMvc.perform(get(Endpoint.Point.GET_FULL, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponseObject,
                        objectMapper.readValue(result.getResponse().getContentAsString(), DefaultApiErrorResponse.class)
                )
        );
    }
}
