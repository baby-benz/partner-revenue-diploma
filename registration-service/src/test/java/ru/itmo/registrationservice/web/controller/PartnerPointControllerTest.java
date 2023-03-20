package ru.itmo.registrationservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.dto.response.DefaultApiErrorResponse;
import ru.itmo.registrationservice.configuration.MessageSourceConfiguration;
import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;
import ru.itmo.registrationservice.domain.enumeration.PartnerType;
import ru.itmo.registrationservice.web.dto.request.CreatePartnerPointRequest;
import ru.itmo.registrationservice.web.dto.request.CreatePartnerProfileRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {MessageSourceConfiguration.class})
@AutoConfigureMockMvc
public class PartnerPointControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MessageSource messageSource;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void initObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void when_postNewPoint_then_created() throws Exception {
        final String partnerProfileName = "partnerSample";
        final PartnerType partnerType = PartnerType.PAYMENT_PARTNER;

        final CreatePartnerProfileRequest profileRequestObject = new CreatePartnerProfileRequest(
                partnerProfileName,
                partnerType
        );
        final String jsonProfileRequestObject = objectMapper.writeValueAsString(profileRequestObject);

        final AtomicReference<String> createdProfileIdReference = new AtomicReference<>();

        mockMvc.perform(post(Endpoint.POST_NEW_PARTNER_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProfileRequestObject)
        ).andDo(
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                setReferenceValueAndGet(
                                        createdProfileIdReference,
                                        JsonPath.read(result.getResponse().getContentAsString(), "$.partnerProfileId")
                                )
                        )
                )
        );

        final String pointName = "pointSample";
        final PartnerPointType pointType = PartnerPointType.PAYMENT_RECEPTION;

        final CreatePartnerPointRequest pointRequestObject = new CreatePartnerPointRequest(
                createdProfileIdReference.get(),
                pointName,
                pointType
        );
        final String jsonPointRequestObject = objectMapper.writeValueAsString(pointRequestObject);

        mockMvc.perform(post(Endpoint.POST_NEW_PARTNER_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPointRequestObject)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.partnerPointId")
                        )
                ),
                jsonPath("$.partnerProfileId").value(createdProfileIdReference.get()),
                jsonPath("$.name").value(pointName),
                jsonPath("$.partnerPointType").value(pointType.name())
        );
    }

    @Test
    public void when_postNewPointWithWrongPartnerProfileId_then_notFoundResponse() throws Exception {
        final String partnerProfileId = "wrongId";
        final String pointName = "pointSample";
        final PartnerPointType pointType = PartnerPointType.PAYMENT_RECEPTION;

        final CreatePartnerPointRequest pointRequestObject = new CreatePartnerPointRequest(
                partnerProfileId,
                pointName,
                pointType
        );
        final String jsonPointRequestObject = new ObjectMapper().writeValueAsString(pointRequestObject);

        final String expectedMessageCode = NotFoundErrorCause.PARTNER_PROFILE_NOT_FOUND.getMessageCode();
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{partnerProfileId},
                Locale.ENGLISH
        );
        final DefaultApiErrorResponse expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
        );

        mockMvc.perform(post(Endpoint.POST_NEW_PARTNER_POINT)
                .contentType(MediaType.APPLICATION_JSON)
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
    public void when_postNewPointWithWrongPartnerProfileIdAndRuLangHeader_then_notFoundResponse() throws Exception {
        final String partnerProfileId = "wrongId";
        final String pointName = "pointSample";
        final PartnerPointType pointType = PartnerPointType.PAYMENT_RECEPTION;

        final CreatePartnerPointRequest pointRequestObject = new CreatePartnerPointRequest(
                partnerProfileId,
                pointName,
                pointType
        );
        final String jsonPointRequestObject = new ObjectMapper().writeValueAsString(pointRequestObject);

        final String expectedMessageCode = NotFoundErrorCause.PARTNER_PROFILE_NOT_FOUND.getMessageCode();
        final Locale russianLocale = new Locale("ru", "RU");
        final String expectedResponseMessage = messageSource.getMessage(
                expectedMessageCode,
                new String[]{partnerProfileId},
                russianLocale
        );
        final DefaultApiErrorResponse expectedResponseObject = new DefaultApiErrorResponse(
                expectedMessageCode,
                expectedResponseMessage
        );

        mockMvc.perform(post(Endpoint.POST_NEW_PARTNER_POINT)
                .contentType(MediaType.APPLICATION_JSON)
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

    private String setReferenceValueAndGet(AtomicReference<String> stringReference, String value) {
        stringReference.set(value);
        return stringReference.get();
    }
}
