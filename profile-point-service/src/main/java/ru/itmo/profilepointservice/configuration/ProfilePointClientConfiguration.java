package ru.itmo.profilepointservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.profilepointservice.web.client.DefaultProfilePointClient;

@RequiredArgsConstructor
@ConditionalOnProperty(value="profile-point.server.url")
@Configuration
public class ProfilePointClientConfiguration {
    @Value("${profile-point.server.url}")
    private String profilePointServerUrl;
    private final ReactorClientHttpConnector clientHttpConnector;

    @Bean
    public ProfilePointClient profilePointClient() {
        return new DefaultProfilePointClient(clientHttpConnector, profilePointServerUrl);
    }
}
