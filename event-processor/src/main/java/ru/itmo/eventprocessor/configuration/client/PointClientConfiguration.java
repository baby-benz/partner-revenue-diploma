package ru.itmo.eventprocessor.configuration.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.common.web.client.PointClient;
import ru.itmo.point.web.client.DefaultPointClient;

@RequiredArgsConstructor
@Configuration
public class PointClientConfiguration {
    @Value("${point.server.port}")
    private int profileServerPort;
    private final ReactorClientHttpConnector clientHttpConnector;

    @Bean
    public PointClient pointClient() {
        return new DefaultPointClient(clientHttpConnector, profileServerPort);
    }
}
