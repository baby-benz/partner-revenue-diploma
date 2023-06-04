package ru.itmo.calcschemeservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.calcschemeservice.web.client.DefaultCalcSchemeClient;
import ru.itmo.common.web.client.CalcSchemeClient;

@RequiredArgsConstructor
@ConditionalOnProperty(value="calc-scheme.server.url")
@Configuration
public class CalcSchemeClientConfiguration {
    @Value("${calc-scheme.server.url}")
    private String calcSchemeServerUrl;
    private final ReactorClientHttpConnector clientHttpConnector;

    @Bean
    public CalcSchemeClient calcSchemeClient() {
        return new DefaultCalcSchemeClient(clientHttpConnector, calcSchemeServerUrl);
    }
}
