package com.ancas.reactive.ws.users.application.ports;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import io.micrometer.observation.ObservationFilter;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserPort {
    Mono<UserInformation> createUser(Mono<UserInformation> userInformation);
    Mono<UserInformation> getUserById(UUID userId);
}
