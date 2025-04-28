package com.ancas.reactive.ws.users.infrastructure.entrypoints.presentation;

import com.ancas.reactive.ws.users.application.ports.IUserPort;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.config.TestSecurityConfig;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.UserResponse;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.utils.ValidationUtils;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(UserController.class)
@Import({TestSecurityConfig.class})
class UserControllerTest {

    @MockitoBean
    private ValidationUtils<Object> validationUtils;
    @MockitoBean
    private IUserPort userPort;
    @Autowired
    private WebTestClient webTestClient;
    private CreateUserRequest createUserRequest;
    private UserInformation userResponse;

    @BeforeEach
    void setUp(){
        createUserRequest = new CreateUserRequest(
                UUID.randomUUID(),
                "John",
                "Doe",
                "john@email.com",
                "password123"
        );
        userResponse = new UserInformation(
                UUID.randomUUID(),
                "John",
                "Doe",
                "john@email.com",
                "password123"
        );
    }

    @Test
    void createUser(){
        //Arrange
        doNothing().when(validationUtils).validate(any(CreateUserRequest.class));
        when(userPort.createUser(any())).thenReturn(Mono.just(userResponse));
        //Act
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(createUserRequest.getFirstName(), response.getFirstName());
                    assertEquals(createUserRequest.getLastName(), response.getLastName());
                    assertEquals(createUserRequest.getEmail(), response.getEmail());
                });
        //Assert
        verify(userPort, times(1)).createUser(any());
    }

    @Test
    void createUserFail(){
        //Arrange
        doThrow(new ConstraintViolationException(Collections.emptySet())).when(validationUtils).validate(any(CreateUserRequest.class));
        //Act
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserRequest)
                .exchange()
                .expectStatus().is5xxServerError();
        //Assert
    }
}