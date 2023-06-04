package ru.itmo.common.web.client;

import ru.itmo.common.web.dto.response.calcscheme.CalcScheme;

public interface CalcSchemeClient {
    void checkCalcSchemeExistence(String calcSchemeId);
    CalcScheme getCalcScheme(String calcSchemeId);
}
