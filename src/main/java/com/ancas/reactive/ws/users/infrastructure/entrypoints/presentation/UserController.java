package com.ancas.reactive.ws.users.infrastructure.entrypoints.presentation;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.mapper.UserMapper;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.UserResponse;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ValidationUtils<Object> validationUtils;
    private final IUserPort userPort;

    public UserController(ValidationUtils<Object> validationUtils, IUserPort userPort) {
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
    @PreAuthorize("authentication.principal.equals(#userId.toString()) or hasRole('ROLE_ADMIN')")
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

    @GetMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getUsersStream(){
        return Flux.interval(Duration.ofSeconds(1))
                .map("Event %s"::formatted);
        /*return this.userPort.streamUser()
                .map(UserMapper::toResponse);*/
    }
}
