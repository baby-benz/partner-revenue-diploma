package ru.itmo.profile.web.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.profile.constant.InternalEndpoint;

public class DefaultProfileClient implements ProfileClient {
    private final WebClient client;

    public DefaultProfileClient(ReactorClientHttpConnector clientHttpConnector, int profileServerPort) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl("http://localhost:" + profileServerPort)
                .build();
    }

    @Override
    public void checkProfileExistence(String profileId) throws HttpStatusCodeException {
        var uriSpec = client.head().uri(InternalEndpoint.HEAD_CHECK_PROFILE, profileId);

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(NotFoundErrorCause.PROFILE_NOT_FOUND, profileId);
        }).bodyToMono(Void.class).block();
    }
}
