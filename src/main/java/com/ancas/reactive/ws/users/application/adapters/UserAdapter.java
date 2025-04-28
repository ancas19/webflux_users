package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.models.Albums;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Objects;
import java.util.UUID;

import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.*;

@Service
public class UserAdapter implements IUserPort {
    private static final Logger log= LoggerFactory.getLogger(UserAdapter.class);
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final Sinks.Many<UserInformation> userSink;
    private final WebClient webClient;

    public UserAdapter(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, Sinks.Many<UserInformation> userSink, WebClient webClient) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.userSink = userSink;
        this.webClient = webClient;
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
    public Mono<UserInformation> getUserById(UUID userId,String include,String authorization) {
        return this.userRepositoryPort.getUserById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException(ERROR_MESSAGE_USER_NOT_FOUND.getMessage().formatted(userId))))
                .flatMap(user->this.addUserAlbums(user,include,authorization));
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

    private Mono<UserInformation> addUserAlbums(UserInformation user, String include,String authorization) {
        if(Objects.isNull(include) || !include.equalsIgnoreCase("albums")) {
            return Mono.just(user);
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/albums")
                        .queryParam("userId", user.getId())
                        .build()
                )
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    log.error("Error while calling albums service: {}", clientResponse.statusCode());
                    return Mono.error(new BadRequestException(ERROR_MESSAGE_ALBUMS_SERVICE.getMessage()));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    log.error("Error while calling albums service: {} ", clientResponse.statusCode());
                    return Mono.error(new BadRequestException(ERROR_MESSAGE_ALBUMS_SERVICE.getMessage()));
                })
                .bodyToFlux(Albums.class)
                .collectList()
                .map(albums -> {
                    user.setAlbums(albums);
                    return user;
                })
                .onErrorResume(error -> {
                    log.error("Error while calling albums service: {}", error.getMessage());
                    return Mono.just(user);
                });

    }
}
