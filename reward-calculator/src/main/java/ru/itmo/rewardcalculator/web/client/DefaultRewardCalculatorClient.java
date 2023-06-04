package ru.itmo.rewardcalculator.web.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import ru.itmo.common.constant.InternalEndpoint;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.exception.cause.HttpErrorCause;
import ru.itmo.common.exception.cause.NotFoundErrorCause;
import ru.itmo.common.web.client.RewardCalculatorClient;
import ru.itmo.common.web.dto.request.PeriodFilter;
import ru.itmo.common.web.dto.response.rewardcalculator.MaxReward;
import ru.itmo.common.web.dto.response.rewardcalculator.MinReward;
import ru.itmo.common.web.dto.response.rewardcalculator.Totals;
import ru.itmo.common.web.dto.response.rewardcalculator.ExtremeReward;

import java.math.BigDecimal;
import java.util.List;

public class DefaultRewardCalculatorClient implements RewardCalculatorClient {
    private final WebClient client;

    public DefaultRewardCalculatorClient(ReactorClientHttpConnector clientHttpConnector, String rewardCalculatorServerUrl) {
        this.client = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl(rewardCalculatorServerUrl)
                .build();
    }

    @Override
    public Totals getTotals(PeriodFilter filter) {
        var uriSpec = client.get().uri(InternalEndpoint.RewardCalculator.GET_TOTALS);

        return uriSpec.retrieve().bodyToMono(Totals.class).block();
    }

    @Override
    public Totals getProfileTotals(String profileId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_TOTALS)
                .queryParam("profileId", profileId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }).bodyToMono(Totals.class).block();
    }

    @Override
    public Totals getPointTotals(String pointId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_TOTALS)
                .queryParam("pointId", pointId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.POINT_NOT_FOUND)),
                    pointId
            );
        }).bodyToMono(Totals.class).block();
    }

    @Override
    public BigDecimal getAvg(PeriodFilter filter) {
        var uriSpec = client.get().uri(InternalEndpoint.RewardCalculator.GET_TOTALS);

        return uriSpec.retrieve().bodyToMono(BigDecimal.class).block();
    }

    @Override
    public BigDecimal getProfileAvg(String profileId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_AVG)
                .queryParam("profileId", profileId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }).bodyToMono(BigDecimal.class).block();
    }

    @Override
    public BigDecimal getPointAvg(String pointId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_AVG)
                .queryParam("pointId", pointId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.POINT_NOT_FOUND)),
                    pointId
            );
        }).bodyToMono(BigDecimal.class).block();
    }

    @Override
    public MaxReward getMaxReward(PeriodFilter filter) {
        return null;
    }

    @Override
    public MaxReward getProfileMaxReward(String profileId, PeriodFilter filter) {
        return null;
    }

    @Override
    public MaxReward getPointMaxReward(String pointId, PeriodFilter filter) {
        return null;
    }

    @Override
    public MinReward getMinReward(PeriodFilter filter) {
        return null;
    }

    @Override
    public MinReward getProfileMinReward(String profileId, PeriodFilter filter) {
        return null;
    }

    @Override
    public MinReward getPointMinReward(String pointId, PeriodFilter filter) {
        return null;
    }

    @Override
    public ExtremeReward getExtremes(PeriodFilter filter) {
        var uriSpec = client.get().uri(InternalEndpoint.RewardCalculator.GET_EXTREMES);

        return uriSpec.retrieve().bodyToMono(ExtremeReward.class).block();
    }

    @Override
    public ExtremeReward getProfileExtremes(String profileId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_EXTREMES)
                .queryParam("profileId", profileId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.PROFILE_NOT_FOUND)),
                    profileId
            );
        }).bodyToMono(ExtremeReward.class).block();
    }

    @Override
    public ExtremeReward getPointExtremes(String pointId, PeriodFilter filter) {
        var uriSpec = client.get().uri(uriBuilder -> uriBuilder
                .path(InternalEndpoint.RewardCalculator.GET_EXTREMES)
                .queryParam("pointId", pointId)
                .build());

        return uriSpec.retrieve().onStatus(status -> status.equals(HttpStatus.NOT_FOUND), onNotFound -> {
            throw new HttpStatusCodeException(
                    new NotFoundErrorCause(List.of(HttpErrorCause.NotFound.POINT_NOT_FOUND)),
                    pointId
            );
        }).bodyToMono(ExtremeReward.class).block();
    }
}
