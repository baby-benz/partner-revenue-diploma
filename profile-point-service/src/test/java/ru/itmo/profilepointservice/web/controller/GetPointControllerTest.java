package ru.itmo.profilepointservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.profilepointservice.web.dto.response.FullPointResponse;
import ru.itmo.profilepointservice.web.test.Data;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GetPointControllerTest extends PointControllerTest {
    @Test
    void when_getFullPoint_then_ok() throws Exception {
        final ProfileAndPointId profileAndPointId = createSamplePoint();

        final FullPointResponse expectedPointResponse = new FullPointResponse(
                profileAndPointId.pointId(),
                profileAndPointId.profileId(),
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        );

        mockMvc.perform(get(Endpoint.Point.GET_FULL, profileAndPointId.pointId())
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
        String pointId = UUID.randomUUID().toString();

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
