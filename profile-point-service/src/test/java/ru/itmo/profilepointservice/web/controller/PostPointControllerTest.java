package ru.itmo.profilepointservice.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.profilepointservice.web.dto.request.CreatePointRequest;
import ru.itmo.profilepointservice.web.test.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PostPointControllerTest extends PointControllerTest {
    @Test
    void when_postNewPoint_then_created() throws Exception {
        final String jsonCreateProfileRequest = objectMapper.writeValueAsString(Data.SAMPLE_PROFILE_REQUEST);

        String profileResponseString = mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonCreateProfileRequest)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        String profileId = JsonPath.read(profileResponseString, "$.profileId");

        final var pointRequestObject = new CreatePointRequest(
                profileId,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        );
        final String jsonPointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

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
                jsonPath("$.profileId").value(profileId),
                jsonPath("$.name").value(Data.SAMPLE_POINT_NAME),
                jsonPath("$.pointType").value(Data.SAMPLE_POINT_TYPE.name()),
                jsonPath("$.status").value(Data.SAMPLE_STATUS.name())
        );
    }

    @Test
    void when_postNewPoint_with_wrongProfileId_then_notFoundResponse() throws Exception {
        final String wrongProfileId = UUID.randomUUID().toString();

        final var pointRequestObject = new CreatePointRequest(
                wrongProfileId,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        );
        final String jsonPointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

        final String expectedMessageCode = HttpErrorCause.NotFound.PROFILE_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{wrongProfileId},
                Locale.ENGLISH
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                List.of(expectedMessageCode),
                List.of(expectedResponseMessage)
        );

        doThrow(new HttpStatusCodeException(new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)), wrongProfileId))
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
    void when_postNewPoint_with_wrongProfileId_and_ruLangHeader_then_notFoundResponse() throws Exception {
        final String wrongProfileId = UUID.randomUUID().toString();

        final var pointRequestObject = new CreatePointRequest(
                wrongProfileId,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        );
        final String jsonPointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

        final String expectedMessageCode = HttpErrorCause.NotFound.PROFILE_NOT_FOUND.getMessageCode();
        final var russianLocale = new Locale("ru", "RU");
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{wrongProfileId},
                russianLocale
        );
        final var expectedResponseObject = new DefaultApiErrorResponse(
                List.of(expectedMessageCode),
                List.of(expectedResponseMessage)
        );

        doThrow(new HttpStatusCodeException(new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)), wrongProfileId))
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
