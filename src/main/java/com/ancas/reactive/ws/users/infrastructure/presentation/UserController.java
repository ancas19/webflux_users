package com.ancas.reactive.ws.users.infrastructure.presentation;

import com.ancas.reactive.ws.users.infrastructure.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.response.UserResponse;
import com.ancas.reactive.ws.users.infrastructure.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ValidationUtils validationUtils;

    public UserController(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    @PostMapping
    public Mono<UserResponse> createUser(@RequestBody Mono<CreateUserRequest> createUserRequest) {
        return createUserRequest
                .doOnNext(user->this.validationUtils.validate(user))
                .doOnNext(user -> System.out.println("Valid: " + user))
                .map(savedUser ->new UserResponse());
    }
}
