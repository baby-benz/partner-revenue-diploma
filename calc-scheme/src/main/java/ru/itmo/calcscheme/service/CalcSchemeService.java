package ru.itmo.calcscheme.service;

import ru.itmo.calcscheme.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcscheme.service.so.out.CreatedCalcSchemeSO;

public interface CalcSchemeService {
    CreatedCalcSchemeSO createCalcScheme(CreateCalcSchemeSO schemeData);
    boolean profileExists(String calcSchemeId);
}
