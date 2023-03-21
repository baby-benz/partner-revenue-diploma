package ru.itmo.partnerprofileservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.partnerprofileservice.service.PartnerPointService;
import ru.itmo.partnerprofileservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.partnerprofileservice.service.so.out.CreatedPartnerPointSO;
import ru.itmo.partnerprofileservice.web.dto.request.CreatePartnerPointRequest;
import ru.itmo.partnerprofileservice.web.dto.response.CreatedPartnerPointResponse;

@RequiredArgsConstructor
@RestController
public class PartnerPointController {
    private final PartnerPointService partnerPointService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = Endpoint.POST_NEW_PARTNER_POINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreatedPartnerPointResponse addPartnerPoint(@RequestBody CreatePartnerPointRequest partnerPointRequest) {
        CreatedPartnerPointSO createdPartnerPoint = partnerPointService.createPartnerPoint(
                new CreatePartnerPointSO(
                        partnerPointRequest.partnerProfileId(),
                        partnerPointRequest.name(),
                        partnerPointRequest.partnerPointType()
                )
        );

        return new CreatedPartnerPointResponse(
                createdPartnerPoint.partnerPointId(),
                createdPartnerPoint.partnerProfileId(),
                createdPartnerPoint.name(),
                createdPartnerPoint.partnerPointType()
        );
    }
}
