package ru.itmo.profile.web.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.profile.constant.InternalEndpoint;

@RequiredArgsConstructor
public class DefaultProfileClient implements ProfileClient {
    private final ReactorClientHttpConnector clientHttpConnector;
    private final int profileServerPort;

    @Override
    public void checkProfile(String profileId) throws HttpStatusCodeException {
        var client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl("http://localhost:" + profileServerPort)
                .build();
        var uriSpec = client.head().uri(InternalEndpoint.GET_PROFILE_EXISTS, profileId);

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(NotFoundErrorCause.PROFILE_NOT_FOUND, profileId);
        }).bodyToMono(Void.class).block();
    }
}
