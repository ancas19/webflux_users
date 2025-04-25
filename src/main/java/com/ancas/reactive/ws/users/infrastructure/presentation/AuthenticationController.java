package com.ancas.reactive.ws.users.infrastructure.presentation;

import com.ancas.reactive.ws.users.application.ports.IAuthenticationPort;
import com.ancas.reactive.ws.users.domain.mapper.AuthenticationMapper;
import com.ancas.reactive.ws.users.infrastructure.request.AuthenticationRequest;
import com.ancas.reactive.ws.users.infrastructure.response.AuthenticationResponse;
import com.ancas.reactive.ws.users.infrastructure.utils.ValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final ValidationUtils<Object> validationUtils;
    private final IAuthenticationPort authenticationPort;

    public AuthenticationController(ValidationUtils<Object> validationUtils, IAuthenticationPort authenticationPort) {
        this.validationUtils = validationUtils;
        this.authenticationPort = authenticationPort;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody Mono<AuthenticationRequest> authRequest){
        return authRequest
                .doOnNext(this.validationUtils::validate)
                .map(AuthenticationMapper::toAuthentication)
                .transform(this.authenticationPort::authenticate)
                .map(AuthenticationMapper::toAuthenticationResponse)
                .map(ResponseEntity::ok);
    }
}
