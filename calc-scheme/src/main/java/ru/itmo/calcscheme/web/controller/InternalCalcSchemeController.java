package ru.itmo.calcscheme.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.calcscheme.constant.InternalEndpoint;
import ru.itmo.calcscheme.service.CalcSchemeService;

@Validated
@RequiredArgsConstructor
@RestController
public class InternalCalcSchemeController {
    private final CalcSchemeService calcSchemeService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(InternalEndpoint.HEAD_CHECK_CALC_SCHEME)
    public void checkCalcScheme(@NotBlank @PathVariable String calcSchemeId) {
        if (!calcSchemeService.profileExists(calcSchemeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
