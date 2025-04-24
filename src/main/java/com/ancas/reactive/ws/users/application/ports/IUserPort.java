package com.ancas.reactive.ws.users.application.ports;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import reactor.core.publisher.Mono;

public interface IUserPort {
    Mono<UserInformation> createUser(Mono<UserInformation> userInformation);
}
