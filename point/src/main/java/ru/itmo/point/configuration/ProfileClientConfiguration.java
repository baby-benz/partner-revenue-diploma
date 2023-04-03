package ru.itmo.point.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.common.web.client.ProfileClient;
import ru.itmo.profile.web.client.DefaultProfileClient;

@RequiredArgsConstructor
@Configuration
public class ProfileClientConfiguration {
    @Value("${profile.server.port}")
    private int profileServerPort;
    private final ReactorClientHttpConnector clientHttpConnector;

    @Bean
    public ProfileClient profileClient() {
        return new DefaultProfileClient(clientHttpConnector, profileServerPort);
    }
}
