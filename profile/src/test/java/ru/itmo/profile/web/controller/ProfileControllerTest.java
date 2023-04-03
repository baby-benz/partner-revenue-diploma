package ru.itmo.profile.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

@AutoConfigureMockMvc
abstract class ProfileControllerTest {
    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    final String sampleProfileName = "sample";
    final ProfileType sampleProfileType = ProfileType.PAYMENT_PARTNER;
    final Status sampleStatus = Status.INACTIVE;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
    }
}
