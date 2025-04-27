package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.*;

@Service
public class UserAdapter implements IUserPort {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final Sinks.Many<UserInformation> userSink;

    public UserAdapter(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, Sinks.Many<UserInformation> userSink) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.userSink = userSink;
    }

    @Override
    public Mono<UserInformation> createUser(Mono<UserInformation> userInformation) {
        return userInformation
                .zipWhen(user -> userRepositoryPort.existsByEmail(user.getEmail()))
                .flatMap(tuple -> {
                    UserInformation user = tuple.getT1();
                    Boolean emailExists = tuple.getT2();
                    if (emailExists) {
                        return Mono.error(new BadRequestException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS.getMessage().formatted(user.getEmail())));
                    }
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepositoryPort.save(user);
                })
                .doOnSuccess(userSink::tryEmitNext);

    }

    @Override
    public Mono<UserInformation> getUserById(UUID userId) {
        return this.userRepositoryPort.getUserById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException(ERROR_MESSAGE_USER_NOT_FOUND.getMessage().formatted(userId))));
    }

    @Override
    public Flux<UserInformation> getAllUsers(int page, int size) {
        return this.userRepositoryPort.getAllUsers(page, size)
                .switchIfEmpty(Mono.error(new NotFoundException(ERROR_MESSAGE_USERS_NOT_FOUND.getMessage())));
    }

    @Override
    public Flux<UserInformation> streamUser() {
        return this.userSink.asFlux()
                .replay(1)
                .autoConnect();
    }
}
