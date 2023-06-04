package ru.itmo.profilepointservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.profilepointservice.web.dto.response.UpdatedPointResponse;
import ru.itmo.profilepointservice.web.test.Data;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PatchPointControllerTest extends PointControllerTest {
    @MockBean
    private CalcSchemeClient calcSchemeClient;

    private final String sampleCalcSchemeId = UUID.randomUUID().toString();

    @Test
    void when_patchPointCalcScheme_then_ok() throws Exception {
        final ProfileAndPointId profileAndPointId = createSamplePoint();

        final UpdatedPointResponse expectedResponse = new UpdatedPointResponse(
                profileAndPointId.pointId(),
                profileAndPointId.profileId(),
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                sampleCalcSchemeId
        );

        mockMvc.perform(patch(Endpoint.Point.PATCH_CALC_SCHEME, profileAndPointId.pointId())
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
        final ProfileAndPointId profileAndPointId = createSamplePoint();
        final String calcSchemeId = UUID.randomUUID().toString();

        final String expectedMessageCode = HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{calcSchemeId},
                Locale.ENGLISH
        );
        final var expectedResponse = new DefaultApiErrorResponse(
                List.of(expectedMessageCode),
                List.of(expectedResponseMessage)
        );

        doThrow(new HttpStatusCodeException(new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND)), calcSchemeId))
                .when(calcSchemeClient)
                .checkCalcSchemeExistence(Mockito.anyString());

        mockMvc.perform(patch(Endpoint.Point.PATCH_CALC_SCHEME, profileAndPointId.pointId())
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
