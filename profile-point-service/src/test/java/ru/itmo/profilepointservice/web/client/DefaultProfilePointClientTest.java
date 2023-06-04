package ru.itmo.profilepointservice.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;
import ru.itmo.profilepointservice.web.dto.request.CreatePointRequest;
import ru.itmo.profilepointservice.web.dto.request.CreateProfileRequest;
import ru.itmo.profilepointservice.web.test.Data;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class DefaultProfilePointClientTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReactorClientHttpConnector clientHttpConnector;
    @Value("${server.port}")
    private int serverPort;
    private ProfilePointClient profilePointClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void initProfileClient() {
        profilePointClient = new DefaultProfilePointClient(clientHttpConnector, "http://localhost:" + serverPort);
        objectMapper = new ObjectMapper();
    }

    @Test
    void when_createProfile_and_checkProfile_then_ok() throws Exception {
        final String profileName = "sample";
        final ProfileType profileType = ProfileType.PAYMENT_PARTNER;
        final Status status = Status.INACTIVE;

        final CreateProfileRequest requestObject = new CreateProfileRequest(profileName, profileType, status);
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

        var result = mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andReturn();

        String profileId = JsonPath.read(result.getResponse().getContentAsString(), "$.profileId");

        assertDoesNotThrow(() -> profilePointClient.checkProfileExistence(profileId));
    }

    @Test
    void given_wrongProfile_when_checkProfile_then_notFoundException() {
        var exception = assertThrows(
                HttpStatusCodeException.class, () -> profilePointClient.checkProfileExistence(UUID.randomUUID().toString())
        );
        assertEquals(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND.getMessageCode()), exception.getErrorCause().getMessageCodes());
    }

    @Test
    void when_createPoint_and_checkPointAndProfileMatch_then_ok() throws Exception {
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

        final String pointId = JsonPath.read(pointResponseString, "$.pointId");

        assertTrue(() -> profilePointClient.pointAndProfileMatches(pointId, profileId));
    }

    @Test
    void given_wrongPoint_when_checkProfile_then_notFoundException() {
        assertFalse(profilePointClient.pointAndProfileMatches(
                        UUID.randomUUID().toString(), UUID.randomUUID().toString()
                )
        );
    }
}
