package ru.itmo.profile.web.client;

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
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.profile.domain.enumeration.ProfileType;
import ru.itmo.profile.web.dto.request.CreateProfileRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class DefaultProfileClientTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReactorClientHttpConnector clientHttpConnector;
    @Value("${server.port}")
    private int serverPort;
    private ProfileClient profileClient;

    @BeforeEach
    void initProfileClient() {
        profileClient = new DefaultProfileClient(clientHttpConnector, serverPort);
    }

    @Test
    void when_createProfile_and_checkProfile_then_ok() throws Exception {
        final String profileName = "sample";
        final ProfileType profileType = ProfileType.PAYMENT_PARTNER;
        final Status status = Status.INACTIVE;

        final CreateProfileRequest requestObject = new CreateProfileRequest(profileName, profileType, status);
        final String jsonRequestObject = new ObjectMapper().writeValueAsString(requestObject);

        var result = mockMvc.perform(post(Endpoint.Profile.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andReturn();

        String profileId = JsonPath.read(result.getResponse().getContentAsString(), "$.profileId");

        assertDoesNotThrow(() -> profileClient.checkProfileExistence(profileId));
    }

    @Test
    void when_noProfile_and_checkProfile_then_notFoundException() {
        var exception = assertThrows(
                HttpStatusCodeException.class, () -> profileClient.checkProfileExistence("wrongId")
        );
        assertEquals(NotFoundErrorCause.PROFILE_NOT_FOUND, exception.getErrorCause());
    }
}
