package ru.itmo.profilepointservice.web.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.profilepointservice.web.dto.request.CreateProfileRequest;
import ru.itmo.profilepointservice.web.test.Data;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PostProfileControllerTest extends ProfileControllerTest {
    @Test
    void when_postNewProfile_then_created() throws Exception {
        final var requestObject = new CreateProfileRequest(
                Data.SAMPLE_PROFILE_NAME,
                Data.SAMPLE_PROFILE_TYPE,
                Data.SAMPLE_STATUS
        );
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

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
                jsonPath("$.name").value(Data.SAMPLE_PROFILE_NAME),
                jsonPath("$.profileType").value(Data.SAMPLE_PROFILE_TYPE.name()),
                jsonPath("$.status").value(Data.SAMPLE_STATUS.name())
        );
    }
}
