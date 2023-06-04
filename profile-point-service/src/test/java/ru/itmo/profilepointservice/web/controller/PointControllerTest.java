package ru.itmo.profilepointservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.profilepointservice.web.dto.request.CreatePointRequest;
import ru.itmo.profilepointservice.web.test.Data;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
abstract class PointControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MessageSource messageSource;
    @MockBean
    ProfilePointClient profileClient;

    ObjectMapper objectMapper;

    @BeforeEach
    void initObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    ProfileAndPointId createSamplePoint() throws Exception {
        final String jsonCreateProfileRequest = objectMapper.writeValueAsString(Data.SAMPLE_PROFILE_REQUEST);

        String profileResponseString = mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonCreateProfileRequest)
        ).andReturn().getResponse().getContentAsString();

        String profileId = JsonPath.read(profileResponseString, "$.profileId");

        final String jsonCreatePointRequestObject = objectMapper.writeValueAsString(new CreatePointRequest(
                profileId,
                Data.SAMPLE_POINT_NAME,
                Data.SAMPLE_POINT_TYPE,
                Data.SAMPLE_STATUS,
                null
        ));

        String pointResponseString = mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreatePointRequestObject)
        ).andReturn().getResponse().getContentAsString();

        return new ProfileAndPointId(profileId, JsonPath.read(pointResponseString, "$.pointId"));
    }

    String createSampleProfile() throws Exception {
        final String jsonCreateProfileRequest = objectMapper.writeValueAsString(Data.SAMPLE_PROFILE_REQUEST);

        String profileResponseString = mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonCreateProfileRequest)
        ).andReturn().getResponse().getContentAsString();

        return JsonPath.read(profileResponseString, "$.profileId");
    }

    record ProfileAndPointId(String profileId, String pointId) {
    }
}
