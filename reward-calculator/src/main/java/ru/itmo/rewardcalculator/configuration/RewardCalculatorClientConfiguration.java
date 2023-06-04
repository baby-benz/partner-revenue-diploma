package ru.itmo.rewardcalculator.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.common.web.client.RewardCalculatorClient;
import ru.itmo.rewardcalculator.web.client.DefaultRewardCalculatorClient;

@RequiredArgsConstructor
@ConditionalOnProperty(value="reward-calculator.server.url")
@Configuration
public class RewardCalculatorClientConfiguration {
        @Value("${reward-calculator.server.url}")
        private String rewardCalculatorServerUrl;
        private final ReactorClientHttpConnector clientHttpConnector;

        @Bean
        public RewardCalculatorClient rewardCalculatorClient() {
            return new DefaultRewardCalculatorClient(clientHttpConnector, rewardCalculatorServerUrl);
        }
}
