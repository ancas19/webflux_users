package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IAuthenticationPort;
import com.ancas.reactive.ws.users.application.ports.IJwtPort;
import com.ancas.reactive.ws.users.domain.enums.ErrorMessages;
import com.ancas.reactive.ws.users.domain.exception.BadCredentialsException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.models.Authentication;
import com.ancas.reactive.ws.users.domain.models.Token;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AuthenticationAdapter implements IAuthenticationPort {
    private final Logger log= LoggerFactory.getLogger(AuthenticationAdapter.class);
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final IJwtPort jwtPort;

    public AuthenticationAdapter(ReactiveAuthenticationManager authenticationManager, UserRepositoryPort userRepositoryPort, IJwtPort jwtPort) {
        this.authenticationManager = authenticationManager;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public Mono<Token> authenticate(Mono<Authentication> authenticationRequest) {
        return authenticationRequest
                .flatMap(this::validateAndAuthenticateUser);
    }

    private Mono<Token> validateAndAuthenticateUser(Authentication credentials) {
        return findUserByEmail(credentials.getEmail())
                .flatMap(user -> performAuthentication(credentials, user))
                .onErrorMap(this::handleAuthenticationError);
    }

    private Mono<UserInformation> findUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.ERROR_MESSAGE_USER_NOT_FOUND_BY_EMAIL.getMessage().formatted(email))));
    }

    private Mono<Token> performAuthentication(Authentication credentials, UserInformation user) {
        UsernamePasswordAuthenticationToken authToken =new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        return authenticationManager.authenticate(authToken)
                .map(authenticatedToken -> generateUserToken(user))
                .onErrorMap(e ->e);
    }

    private Token generateUserToken(UserInformation user) {
        String token = jwtPort.generateToken(user.getId().toString());
        return new Token(user.getId(), token);
    }

    private Throwable handleAuthenticationError(Throwable error) {
        log.error("Authentication error occurred: {}", error.getMessage(), error);
        return new BadCredentialsException(ErrorMessages.ERROR_MESSAGE_BAD_CREDENTIALS.getMessage());
    }
}
