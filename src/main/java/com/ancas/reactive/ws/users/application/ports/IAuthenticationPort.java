package com.ancas.reactive.ws.users.application.ports;

import com.ancas.reactive.ws.users.domain.models.Authentication;
import com.ancas.reactive.ws.users.domain.models.Token;
import reactor.core.publisher.Mono;

public interface IAuthenticationPort {

    Mono<Token> authenticate(Mono<Authentication> authentication);
}
