package ru.itmo.calcschemeservice.service;

import ru.itmo.calcschemeservice.service.so.in.CreateCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.CreatedCalcSchemeSO;
import ru.itmo.calcschemeservice.service.so.out.FullCalcSchemeSO;

public interface CalcSchemeService {
    CreatedCalcSchemeSO createCalcScheme(CreateCalcSchemeSO schemeData);
    FullCalcSchemeSO getCalcScheme(String calcSchemeId);
    boolean profileExists(String calcSchemeId);
}
