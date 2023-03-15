/*
package ru.itmo.eventprocessor.configuration.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Value(value = "${kafka.topics.transaction}")
    private String topic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(new HashMap<>(kafkaProperties.buildAdminProperties()));
    }

    @Bean
    public NewTopic transactionsTopic() {
        return new NewTopic("${kafka.topics.transaction}", 1, (short) 1);
    }
}
*/
