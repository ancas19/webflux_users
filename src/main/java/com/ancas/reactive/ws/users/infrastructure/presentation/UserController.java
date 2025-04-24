package com.ancas.reactive.ws.users.infrastructure.presentation;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.mapper.UserMapper;
import com.ancas.reactive.ws.users.infrastructure.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.response.UserResponse;
import com.ancas.reactive.ws.users.infrastructure.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ValidationUtils validationUtils;
    private final IUserPort userPort;

    public UserController(ValidationUtils validationUtils, IUserPort userPort) {
        this.validationUtils = validationUtils;
        this.userPort = userPort;
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody Mono<CreateUserRequest> createUserRequest) {
        return createUserRequest
                .doOnNext(this.validationUtils::validate)
                .map(UserMapper::toUser)
                .transform(this.userPort::createUser)
                .map(UserMapper::toResponse)
                .map(savedUser ->
                        ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(URI.create("/users/%s".formatted(savedUser.getId())))
                        .body(savedUser)
                );
    }


    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable("userId") UUID userId){
        return this.userPort
                .getUserById(userId)
                .map(UserMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping()
    public Flux<UserResponse> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return this.userPort.getAllUsers(page,size)
                .map(UserMapper::toResponse);
    }
}
