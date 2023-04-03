package ru.itmo.point.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.point.web.dto.response.UpdatedPointResponse;
import ru.itmo.point.web.test.Data;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PatchPointCalcSchemeControllerTest extends PointControllerTest {
    @MockBean
    private CalcSchemeClient calcSchemeClient;

    private final String sampleCalcSchemeId = UUID.randomUUID().toString();

    @Test
    void when_patchPointCalcScheme_then_ok() throws Exception {
        final String pointId = createSamplePoint();

        final UpdatedPointResponse expectedResponse = new UpdatedPointResponse(
                pointId,
                Data.SAMPLE_PROFILE_ID,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                sampleCalcSchemeId
        );

        mockMvc.perform(patch(Endpoint.Point.PATCH_CALC_SCHEME, pointId)
                .param("calcSchemeId", sampleCalcSchemeId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                result -> assertEquals(
                        expectedResponse,
                        objectMapper.readValue(result.getResponse().getContentAsString(), UpdatedPointResponse.class)
                )
        );
    }

    @Test
    void when_patchPointCalcScheme_with_wrongCalcSchemeId_then_notFound() throws Exception {
        final String pointId = createSamplePoint();
        final String calcSchemeId = UUID.randomUUID().toString();

        final String expectedMessageCode = NotFoundErrorCause.CALC_SCHEME_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{calcSchemeId},
                Locale.ENGLISH
        );
        final var expectedResponse = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
        );

        doThrow(new HttpStatusCodeException(NotFoundErrorCause.CALC_SCHEME_NOT_FOUND, calcSchemeId))
                .when(calcSchemeClient)
                .checkCalcSchemeExistence(Mockito.anyString());

        mockMvc.perform(patch(Endpoint.Point.PATCH_CALC_SCHEME, pointId)
                .param("calcSchemeId", calcSchemeId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound(),
                result -> assertEquals(
                        expectedResponse,
                        objectMapper.readValue(result.getResponse().getContentAsString(), DefaultApiErrorResponse.class)
                )
        );
    }
}
