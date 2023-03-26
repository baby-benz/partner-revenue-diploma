package ru.itmo.point.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.domain.enumeration.PointType;
import ru.itmo.point.web.dto.request.CreatePointRequest;
import ru.itmo.point.web.dto.request.UpdatePointRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
public class PutPointControllerTest extends PointControllerTest {
    final String profileIdToUpdate = "newId";
    final String pointNameToUpdate = "newName";
    final PointType pointTypeToUpdate = PointType.PRODUCT_SELLING;
    final Status statusToUpdate = Status.ACTIVE;

    @Test
    void when_createPoint_and_putPoint_then_ok() throws Exception {
        final var createPointRequest = new CreatePointRequest(
                sampleProfileId,
                samplePointName,
                samplePointType,
                sampleStatus
        );
        final String jsonCreatePointRequestObject = objectMapper.writeValueAsString(createPointRequest);

        doNothing().when(profileClient).checkProfile(Mockito.anyString());

        var result = mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreatePointRequestObject)
        ).andReturn();

        final String pointId = JsonPath.read(result.getResponse().getContentAsString(), "$.pointId");

        final var updatePointRequest = new UpdatePointRequest(
                profileIdToUpdate,
                pointNameToUpdate,
                pointTypeToUpdate,
                statusToUpdate
        );
        final String jsonUpdatePointRequestObject = objectMapper.writeValueAsString(updatePointRequest);

        mockMvc.perform(put(Endpoint.Point.PUT, pointId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdatePointRequestObject)
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.pointId").value(pointId),
                jsonPath("$.profileId").value(profileIdToUpdate),
                jsonPath("$.name").value(pointNameToUpdate),
                jsonPath("$.pointType").value(pointTypeToUpdate.name()),
                jsonPath("$.status").value(statusToUpdate.name())
        );
    }

    @Test
    void when_putPointWithWrongPointId_then_notFoundResponse() throws Exception {
        doNothing().when(profileClient).checkProfile(Mockito.anyString());

        final var updatePointRequest = new UpdatePointRequest(
                profileIdToUpdate,
                pointNameToUpdate,
                pointTypeToUpdate,
                statusToUpdate
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
    void when_putPointWithWrongPointIdAndRuLangHeader_then_notFoundResponse() throws Exception {
        doNothing().when(profileClient).checkProfile(Mockito.anyString());

        final var updatePointRequest = new UpdatePointRequest(
                profileIdToUpdate,
                pointNameToUpdate,
                pointTypeToUpdate,
                statusToUpdate
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
