package com.ancas.reactive.ws.users.application.ports;

import reactor.core.publisher.Mono;

public interface IJwtPort {
    String generateToken(String subject);
    Mono<Boolean> validateJwt(String token);
    String extractTokenSubject(String token);
}
