package ru.itmo.profilepointservice.web.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.ProfilePointClient;

import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultProfilePointClient implements ProfilePointClient {
    private final WebClient client;

    public DefaultProfilePointClient(ReactorClientHttpConnector clientHttpConnector, String profilePointServerUrl) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl(profilePointServerUrl)
                .build();
    }

    @Override
    public String getProfileIdByPointId(String pointId) {
        return null;
    }

    @Override
    public boolean pointAndProfileMatches(String pointId, String profileId) {
        var uriSpec = client.head().uri(uriBuilder ->
                uriBuilder.path(InternalEndpoint.Point.HEAD_CHECK_POINT)
                        .queryParam("profileId", profileId)
                        .build(pointId)
        );

        return Boolean.TRUE.equals(
                uriSpec.exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.debug(clientResponse.toString());
                        return Mono.just(false);
                    }
                    return Mono.just(true);
                }).block()
        );
    }

    @Override
    public String getCalcSchemeId(String pointId) {
        var uriSpec = client.get().uri(uriBuilder ->
                uriBuilder.path(InternalEndpoint.Point.GET_REQUESTED_POINT_FIELDS)
                        .queryParam("fieldsToInclude", "CALC_SCHEME_ID")
                        .build(pointId)
        );

        Map<String, String> values = uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.POINT_NOT_FOUND)),
                    pointId
            );
        }).bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {}).block();

        return values.get("calcSchemeId");
    }

    @Override
    public void checkProfileExistence(String profileId) throws HttpStatusCodeException {
        var uriSpec = client.head().uri(InternalEndpoint.Profile.HEAD_CHECK_PROFILE, profileId);

        uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }).toBodilessEntity().block();
    }

    @Override
    public List<String> getPointIdsWithProfileId(String profileId) {
        var uriSpec = client.get().uri(uriBuilder ->
                uriBuilder.path(InternalEndpoint.Point.GET_POINT_IDS_WITH_PROFILE_ID)
                        .queryParam("profileId", profileId)
                        .build()
        );

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }).bodyToMono(new ParameterizedTypeReference<List<String>>() {
        }).block();
    }
}
