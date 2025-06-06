package com.ancas.reactive.ws.users.infrastructure.entrypoints.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientconfig {

    @Value("${base-uri}")
    private String baseUri;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
