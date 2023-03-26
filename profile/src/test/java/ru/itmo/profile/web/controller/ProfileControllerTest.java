package ru.itmo.profile.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

@AutoConfigureMockMvc
abstract class ProfileControllerTest {
    @Autowired
    MockMvc mockMvc;

    final String sampleProfileName = "sample";
    final ProfileType sampleProfileType = ProfileType.PAYMENT_PARTNER;
    final Status sampleStatus = Status.INACTIVE;
}
