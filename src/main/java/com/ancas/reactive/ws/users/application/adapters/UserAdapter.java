package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;
import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.ERROR_MESSAGE_USER_NOT_FOUND;

@Service
public class UserAdapter implements IUserPort {
    private final UserRepositoryPort userRepositoryPort;

    public UserAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Mono<UserInformation> createUser(Mono<UserInformation> userInformation) {
        return userInformation
                .zipWhen(user -> userRepositoryPort.existsByEmail(user.getEmail()))
                .flatMap(tuple -> {
                    UserInformation user = tuple.getT1();
                    Boolean emailExists = tuple.getT2();
                    if (emailExists) {
                        return Mono.error(new NotFoundException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS.getMessage().formatted(user.getEmail())));
                    }
                    return userRepositoryPort.save(user);
                });

    }

    @Override
    public Mono<UserInformation> getUserById(UUID userId) {
        return this.userRepositoryPort.getUserById(userId)
                .switchIfEmpty(Mono.error(new BadRequestException(ERROR_MESSAGE_USER_NOT_FOUND.getMessage().formatted(userId))));
    }
}
