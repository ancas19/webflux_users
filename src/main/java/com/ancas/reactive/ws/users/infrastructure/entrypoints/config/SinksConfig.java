package com.ancas.reactive.ws.users.infrastructure.entrypoints.config;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinksConfig {
    @Bean
    public Sinks.Many<UserInformation> userSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
