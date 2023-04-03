package ru.itmo.point.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.PointClient;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.point.web.test.Data;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DefaultPointClientTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReactorClientHttpConnector clientHttpConnector;
    @MockBean
    private ProfileClient profileClient;

    @Value("${server.port}")
    private int serverPort;
    private PointClient pointClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void initCalcSchemeClient() {
        pointClient = new DefaultPointClient(clientHttpConnector, serverPort);
        objectMapper = new ObjectMapper();
    }

    @Test
    void when_createPoint_and_checkPointAndProfileMatch_then_ok() throws Exception {
        final String jsonRequestObject = objectMapper.writeValueAsString(Data.SAMPLE_POINT_REQUEST);

        final var result = mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andReturn();

        final String pointId = JsonPath.read(result.getResponse().getContentAsString(), "$.pointId");
        final String profileId = JsonPath.read(result.getResponse().getContentAsString(), "$.profileId");

        assertDoesNotThrow(() -> pointClient.checkPointAndProfileMatch(pointId, profileId));
    }

    @Test
    void given_wrongPoint_when_checkProfile_then_notFoundException() {
        var exception = assertThrows(
                HttpStatusCodeException.class, () -> pointClient.checkPointAndProfileMatch("wrongId", "wrongId")
        );
        assertEquals(NotFoundErrorCause.POINT_BY_ID_AND_PROFILE_ID_NOT_FOUND, exception.getErrorCause());
    }
}
