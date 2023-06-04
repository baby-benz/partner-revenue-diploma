package ru.itmo.calcschemeservice.web.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.dto.response.calcscheme.CalcScheme;

import java.util.List;

public class DefaultCalcSchemeClient implements CalcSchemeClient {
    private final WebClient client;

    public DefaultCalcSchemeClient(ReactorClientHttpConnector clientHttpConnector, String calcSchemeServerUrl) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl(calcSchemeServerUrl)
                .build();
    }

    @Override
    public void checkCalcSchemeExistence(String calcSchemeId) {
        var uriSpec = client.head().uri(InternalEndpoint.CalcScheme.HEAD_CHECK_CALC_SCHEME, calcSchemeId);

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND)),
                    calcSchemeId
            );
        }).toBodilessEntity().block();
    }

    @Override
    public CalcScheme getCalcScheme(String calcSchemeId) {
        var uriSpec = client.get().uri(InternalEndpoint.CalcScheme.GET_CALC_SCHEME, calcSchemeId);

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.CALC_SCHEME_NOT_FOUND)),
                    calcSchemeId
            );
        }).bodyToMono(CalcScheme.class).block();
    }
}
