package ru.itmo.registrationservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.registrationservice.service.PartnerProfileService;
import ru.itmo.registrationservice.service.so.in.CreatePartnerProfileSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerProfileSO;
import ru.itmo.registrationservice.web.dto.request.CreatePartnerProfileRequest;
import ru.itmo.registrationservice.web.dto.response.CreatedPartnerProfileResponse;

@RequiredArgsConstructor
@RestController
public class PartnerProfileController {
    private final PartnerProfileService partnerProfileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = Endpoint.POST_NEW_PARTNER_PROFILE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreatedPartnerProfileResponse createPartnerProfile(@RequestBody CreatePartnerProfileRequest partnerProfileRequest) {
        CreatedPartnerProfileSO createdPartnerProfile = partnerProfileService.createPartnerProfile(
                new CreatePartnerProfileSO(
                        partnerProfileRequest.name(),
                        partnerProfileRequest.partnerType()
                )
        );

        return new CreatedPartnerProfileResponse(
                createdPartnerProfile.partnerProfileId(),
                createdPartnerProfile.name(),
                createdPartnerProfile.partnerType()
        );
    }
}
