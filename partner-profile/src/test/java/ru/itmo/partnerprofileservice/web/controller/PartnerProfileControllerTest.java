package ru.itmo.partnerprofileservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.partnerprofileservice.domain.enumeration.PartnerType;
import ru.itmo.partnerprofileservice.web.dto.request.CreatePartnerProfileRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PartnerProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void when_postNewProfile_then_created() throws Exception {
        final String partnerProfileName = "sample";
        final PartnerType partnerType = PartnerType.PAYMENT_PARTNER;

        final CreatePartnerProfileRequest requestObject = new CreatePartnerProfileRequest(partnerProfileName, partnerType);
        final String jsonRequestObject = new ObjectMapper().writeValueAsString(requestObject);

        mockMvc.perform(post(Endpoint.POST_NEW_PARTNER_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.partnerProfileId")
                        )
                ),
                jsonPath("$.name").value(partnerProfileName),
                jsonPath("$.partnerType").value(partnerType.name())
        );
    }
}
