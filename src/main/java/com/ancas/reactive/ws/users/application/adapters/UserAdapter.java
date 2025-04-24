package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;

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
                        return Mono.error(new BadRequestException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS.getMessage().formatted(user.getEmail())));
                    }
                    return userRepositoryPort.save(user);
                });

    }
}
