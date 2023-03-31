package ru.itmo.point.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.web.dto.request.CreatePointRequest;
import ru.itmo.point.web.dto.response.FullPointResponse;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GetFullPointControllerTest extends PointControllerTest {
    @Test
    void when_getFullPoint_then_ok() throws Exception {
        final var pointRequestObject = new CreatePointRequest(
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
        );
        final String jsonCreatePointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

        var result = mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreatePointRequestObject)
        ).andReturn();

        final String pointId = JsonPath.read(result.getResponse().getContentAsString(), "$.pointId");

        final FullPointResponse expectedPointResponse = new FullPointResponse(
                pointId,
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
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
    void when_getFullPointWithWrongId_then_ok() throws Exception {
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
