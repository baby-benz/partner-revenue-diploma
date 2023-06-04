package ru.itmo.rewardcalculator.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.constant.Endpoint;
import ru.itmo.common.service.util.TimestampUtil;
import ru.itmo.rewardcalculator.service.RewardService;
import ru.itmo.rewardcalculator.service.so.out.FullRewardSO;
import ru.itmo.rewardcalculator.web.dto.response.FullRewardResponse;

@RequiredArgsConstructor
@RestController
public class RewardController {
    private final RewardService rewardService;
    @GetMapping(value = Endpoint.RewardCalculator.GET_FULL, produces = MediaType.APPLICATION_JSON_VALUE)
    public FullRewardResponse getReward(@PathVariable @NotBlank String rewardId) {
        FullRewardSO reward = rewardService.getReward(rewardId);

        return new FullRewardResponse(
                reward.eventId(),
                reward.eventAmount(),
                reward.rewardAmount(),
                reward.formula(),
                TimestampUtil.toString(reward.timestamp()),
                reward.profileId(),
                reward.pointId(),
                reward.eventId(),
                reward.calcSchemeId()
        );
    }
}
