package com.ancas.reactive.ws.users.infrastructure.entrypoints.security;

import com.ancas.reactive.ws.users.application.ports.IJwtPort;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.filter.JwtAuthenticationFilter;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.filter.SecurityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurity {

    @Value("${spring.webflux.base-path:/api}")
    private String basePath;

    private final SecurityProperties securityProperties;

    public WebSecurity(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityWebFilterChain httpSecurityFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager, IJwtPort jwtService) {
        return http.authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers(HttpMethod.POST, basePath+"/users").permitAll()
                                .pathMatchers(HttpMethod.POST, basePath+"/auth/login").permitAll()
                                .pathMatchers(HttpMethod.GET, basePath+"/users/stream").permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .addFilterAt(new JwtAuthenticationFilter(jwtService,securityProperties.getPublicPaths()), SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
