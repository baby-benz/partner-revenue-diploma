package ru.itmo.registrationservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.itmo.registrationservice.service.PartnerPointService;
import ru.itmo.registrationservice.service.so.in.CreatePartnerPointSO;
import ru.itmo.registrationservice.service.so.out.CreatedPartnerPointSO;
import ru.itmo.registrationservice.web.dto.request.CreatePartnerPointRequest;
import ru.itmo.registrationservice.web.dto.response.CreatedPartnerPointResponse;

@RequiredArgsConstructor
@RestController("partner-point")
public class PartnerPointController {
    private final PartnerPointService partnerPointService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
