package ru.itmo.point.web.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.PointClient;
import ru.itmo.point.constant.InternalEndpoint;

public class DefaultPointClient implements PointClient {
    private final WebClient client;

    public DefaultPointClient(ReactorClientHttpConnector clientHttpConnector, int pointServerPort) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl("http://localhost:" + pointServerPort)
                .build();
    }

    @Override
    public void checkPointAndProfileMatch(String pointId, String profileId) {
        var uriSpec = client.head().uri(uriBuilder ->
                uriBuilder.path(InternalEndpoint.HEAD_CHECK_POINT)
                        .queryParam("profileId", profileId)
                        .build(pointId)
        );

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    NotFoundErrorCause.POINT_BY_ID_AND_PROFILE_ID_NOT_FOUND,
                    pointId,
                    profileId
            );
        }).bodyToMono(Void.class).block();
    }
}
