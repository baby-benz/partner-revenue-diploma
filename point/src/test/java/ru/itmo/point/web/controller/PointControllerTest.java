package ru.itmo.point.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.point.domain.enumeration.PointType;

@AutoConfigureMockMvc
abstract class PointControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MessageSource messageSource;
    @MockBean
    ProfileClient profileClient;

    final String samplePointName = "pointSample";
    final String sampleProfileId = "profileSample";
    final PointType samplePointType = PointType.PAYMENT_RECEPTION;
    final Status sampleStatus = Status.INACTIVE;

    ObjectMapper objectMapper;

    @BeforeEach
    void initObjectMapper() {
        objectMapper = new ObjectMapper();
    }
}
