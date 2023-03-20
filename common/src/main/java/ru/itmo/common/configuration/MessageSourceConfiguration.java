package ru.itmo.common.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.common.exception.handling.GlobalExceptionHandler;

@Configuration
public class MessageSourceConfiguration {
    @Bean
    public GlobalExceptionHandler exceptionHandler(MessageSource messageSource) {
        return new GlobalExceptionHandler(messageSource);
    }
}
