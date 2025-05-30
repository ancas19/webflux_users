package com.ancas.reactive.ws.users.application.ports;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IUserPort {
    Mono<UserInformation> createUser(Mono<UserInformation> userInformation);
    Mono<UserInformation> getUserById(UUID userId,String include,String authorization);
    Flux<UserInformation> getAllUsers(int page, int size);
    Flux<UserInformation> streamUser();
}
