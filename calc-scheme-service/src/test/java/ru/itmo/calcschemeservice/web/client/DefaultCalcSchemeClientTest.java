package ru.itmo.calcschemeservice.web.client;

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
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcRuleRequest;
import ru.itmo.calcschemeservice.web.dto.request.CreateCalcSchemeRequest;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DefaultCalcSchemeClientTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReactorClientHttpConnector clientHttpConnector;
    @Value("${server.port}")
    private int serverPort;
    private CalcSchemeClient calcSchemeClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void initCalcSchemeClient() {
        calcSchemeClient = new DefaultCalcSchemeClient(clientHttpConnector, "http://localhost:" + serverPort);
        objectMapper = new ObjectMapper();
    }

    @Test
    void when_createCalcScheme_and_checkCalcScheme_then_ok() throws Exception {
        final List<CreateCalcRuleRequest> calcRulesRequestObject = List.of(
                new CreateCalcRuleRequest(0L, 0.1f, 0)
        );

        final CreateCalcSchemeRequest requestObject = new CreateCalcSchemeRequest(calcRulesRequestObject, false);
        final String jsonRequestObject = objectMapper.writeValueAsString(requestObject);

        var result = mockMvc.perform(post(Endpoint.CalcScheme.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestObject)
        ).andReturn();

        String calcSchemeId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        assertDoesNotThrow(() -> calcSchemeClient.checkCalcSchemeExistence(calcSchemeId));
    }

    @Test
    void given_wrongCalcScheme_when_checkCalcScheme_then_notFoundException() {
        var exception = assertThrows(
                HttpStatusCodeException.class, () -> calcSchemeClient.checkCalcSchemeExistence(UUID.randomUUID().toString())
        );
        assertEquals(List.of(HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND.getMessageCode()), exception.getErrorCause().getMessageCodes());
    }
}
