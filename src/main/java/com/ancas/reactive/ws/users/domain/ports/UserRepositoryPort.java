package com.ancas.reactive.ws.users.domain.ports;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<Boolean> existsByEmail(String email);
    Mono<UserInformation> save(UserInformation user);
}
