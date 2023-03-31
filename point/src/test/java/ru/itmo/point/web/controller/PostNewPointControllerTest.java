package ru.itmo.point.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.web.dto.request.CreatePointRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PostNewPointControllerTest extends PointControllerTest {
    @Test
    void when_postNewPoint_then_created() throws Exception {
        final var pointRequestObject = new CreatePointRequest(
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
        );
        final String jsonPointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

        doNothing().when(profileClient).checkProfileExistence(Mockito.anyString());

        mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonPointRequestObject)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.pointId")
                        )
                ),
                jsonPath("$.profileId").value(sampleProfileId),
                jsonPath("$.name").value(samplePointName),
                jsonPath("$.pointType").value(samplePointType.name()),
                jsonPath("$.status").value(sampleStatus.name())
        );
    }

    @Test
    void when_postNewPointWithWrongProfileId_then_notFoundResponse() throws Exception {
        final var pointRequestObject = new CreatePointRequest(
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
        );
        final String jsonPointRequestObject = new ObjectMapper().writeValueAsString(pointRequestObject);

        final String expectedMessageCode = NotFoundErrorCause.PROFILE_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{sampleProfileId},
                Locale.ENGLISH
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
        );

        doThrow(new HttpStatusCodeException(NotFoundErrorCause.PROFILE_NOT_FOUND, sampleProfileId))
                .when(profileClient)
                .checkProfileExistence(Mockito.anyString());

        mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonPointRequestObject)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponseObject,
                        objectMapper.readValue(result.getResponse().getContentAsString(), DefaultApiErrorResponse.class)
                )
        );
    }

    @Test
    void when_postNewPointWithWrongProfileIdAndRuLangHeader_then_notFoundResponse() throws Exception {
        final var pointRequestObject = new CreatePointRequest(
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
        );
        final String jsonPointRequestObject = new ObjectMapper().writeValueAsString(pointRequestObject);

        final String expectedMessageCode = NotFoundErrorCause.PROFILE_NOT_FOUND.getMessageCode();
        final var russianLocale = new Locale("ru", "RU");
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{sampleProfileId},
                russianLocale
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
        );

        doThrow(new HttpStatusCodeException(NotFoundErrorCause.PROFILE_NOT_FOUND, sampleProfileId))
                .when(profileClient)
                .checkProfileExistence(Mockito.anyString());

        mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(russianLocale)
                .content(jsonPointRequestObject)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponseObject,
                        objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), DefaultApiErrorResponse.class)
                )
        );
    }
}
