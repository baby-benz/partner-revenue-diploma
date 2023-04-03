package ru.itmo.point.web.controller;

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
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.point.web.test.Data;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
abstract class PointControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MessageSource messageSource;
    @MockBean
    ProfileClient profileClient;

    ObjectMapper objectMapper;

    @BeforeEach
    void initObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    String createSamplePoint() throws Exception {
        final String jsonCreatePointRequestObject = objectMapper.writeValueAsString(Data.SAMPLE_POINT_REQUEST);

        var result = mockMvc.perform(post(Endpoint.Point.POST_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreatePointRequestObject)
        ).andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.pointId");
    }
}
