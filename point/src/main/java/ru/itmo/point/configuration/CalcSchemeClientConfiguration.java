package ru.itmo.point.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.calcscheme.web.client.DefaultCalcSchemeClient;
import ru.itmo.common.web.client.CalcSchemeClient;

@RequiredArgsConstructor
@Configuration
public class CalcSchemeClientConfiguration {
    @Value("${calc-scheme.server.port}")
    private int calcSchemePort;
    private final ReactorClientHttpConnector clientHttpConnector;

    @Bean
    public CalcSchemeClient calcSchemeClient() {
        return new DefaultCalcSchemeClient(clientHttpConnector, calcSchemePort);
    }
}
