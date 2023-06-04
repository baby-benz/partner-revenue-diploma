package ru.itmo.calcschemeservice.web.controller;

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
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcRuleRequest;
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcSchemeRequest;
import ru.itmo.common.constant.Endpoint;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class CreateCalcSchemeControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MessageSource messageSource;

    final long[] sampleCalcRulesAmount = {0L, 100L, 1000L};
    final float[] sampleCalcRulesInterestRate = {0.1f, 0.01f, 0.001f};
    final int[] sampleCalcRulesBonus = {0, 0, 500};

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void when_createCalcScheme_then_ok() throws Exception {
        final List<CreateCalcRuleRequest> calcRulesRequestObject = List.of(
                new CreateCalcRuleRequest(sampleCalcRulesAmount[0], sampleCalcRulesInterestRate[0], sampleCalcRulesBonus[0]),
                new CreateCalcRuleRequest(sampleCalcRulesAmount[1], sampleCalcRulesInterestRate[1], sampleCalcRulesBonus[1]),
                new CreateCalcRuleRequest(sampleCalcRulesAmount[2], sampleCalcRulesInterestRate[2], sampleCalcRulesBonus[2])
        );

        final var requestObject = new CreateCalcSchemeRequest(calcRulesRequestObject, false);
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

        mockMvc.perform(post(Endpoint.CalcScheme.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andExpectAll(
                status().isCreated(),
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.id")
                        )
                ),
                jsonPath("$.calcRules[0].amount").value(sampleCalcRulesAmount[0]),
                jsonPath("$.calcRules[0].interestRate").value(sampleCalcRulesInterestRate[0]),
                jsonPath("$.calcRules[0].bonus").value(sampleCalcRulesBonus[0]),
                jsonPath("$.calcRules[1].amount").value(sampleCalcRulesAmount[1]),
                jsonPath("$.calcRules[1].interestRate").value(sampleCalcRulesInterestRate[1]),
                jsonPath("$.calcRules[1].bonus").value(sampleCalcRulesBonus[1]),
                jsonPath("$.calcRules[2].amount").value(sampleCalcRulesAmount[2]),
                jsonPath("$.calcRules[2].interestRate").value(sampleCalcRulesInterestRate[2]),
                jsonPath("$.calcRules[2].bonus").value(sampleCalcRulesBonus[2]),
                jsonPath("$.isRecalc").value(false)
        );
    }

    @Test
    void when_createCalcScheme_with_wrongCalcRulesAmount_then_badRequest() throws Exception {
        final List<CreateCalcRuleRequest> calcRulesRequestObject = List.of(
                new CreateCalcRuleRequest(10L, 0.1f, 0),
                new CreateCalcRuleRequest(1L, 0.1f, 0)
        );

        final CreateCalcSchemeRequest requestObject = new CreateCalcSchemeRequest(calcRulesRequestObject, false);
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

        mockMvc.perform(post(Endpoint.CalcScheme.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andExpectAll(
                status().isBadRequest(),
                result -> assertEquals(
                        "Invalid request content.",
                        JsonPath.read(result.getResponse().getContentAsString(), "$.detail")
                )
        );
    }

    @Test
    void given_negativeBonus_when_createCalcScheme_then_badRequest() throws Exception {
        final List<CreateCalcRuleRequest> calcRulesRequestObject = List.of(
                new CreateCalcRuleRequest(0L, 0.1f, -10),
                new CreateCalcRuleRequest(10L, 0.15f, 0)
        );

        final CreateCalcSchemeRequest requestObject = new CreateCalcSchemeRequest(calcRulesRequestObject, false);
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

        mockMvc.perform(post(Endpoint.CalcScheme.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andExpectAll(
                status().isBadRequest(),
                result -> assertEquals(
                        "Invalid request content.",
                        JsonPath.read(result.getResponse().getContentAsString(), "$.detail")
                )
        );
    }
}
