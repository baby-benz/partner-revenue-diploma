package ru.itmo.profile.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;
import ru.itmo.profile.web.dto.request.CreateProfileRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PostNewProfileControllerTest extends ProfileControllerTest {
    @Test
    void when_postNewProfile_then_created() throws Exception {
        final var requestObject = new CreateProfileRequest(sampleProfileName, sampleProfileType, sampleStatus);
        final String jsonRequestObject = new ObjectMapper().writeValueAsString(requestObject);

        mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                result -> assertDoesNotThrow(() ->
                        UUID.fromString(
                                JsonPath.read(result.getResponse().getContentAsString(), "$.profileId")
                        )
                ),
                jsonPath("$.name").value(sampleProfileName),
                jsonPath("$.profileType").value(sampleProfileType.name()),
                jsonPath("$.status").value(sampleStatus.name())
        );
    }
}
