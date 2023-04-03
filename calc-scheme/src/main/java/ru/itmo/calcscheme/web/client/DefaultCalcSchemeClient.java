package ru.itmo.calcscheme.web.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.calcscheme.constant.InternalEndpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.CalcSchemeClient;

public class DefaultCalcSchemeClient implements CalcSchemeClient {
    private final WebClient client;

    public DefaultCalcSchemeClient(ReactorClientHttpConnector clientHttpConnector, int calcSchemeServerPort) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl("http://localhost:" + calcSchemeServerPort)
                .build();
    }

    @Override
    public void checkCalcSchemeExistence(String calcSchemeId) {
        var uriSpec = client.head().uri(InternalEndpoint.HEAD_CHECK_CALC_SCHEME, calcSchemeId);

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(NotFoundErrorCause.CALC_SCHEME_NOT_FOUND, calcSchemeId);
        }).bodyToMono(Void.class).block();
    }
}
