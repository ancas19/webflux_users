package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.domain.models.Albums;
import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.ancas.reactive.ws.users.domain.enums.ErrorMessages.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private Sinks.Many<UserInformation> userSink;
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec getSpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;
    @InjectMocks
    private UserAdapter userAdapter;
    private UserInformation userInformation;

    @BeforeEach
    void setUp(){
        userInformation = new UserInformation(
                UUID.randomUUID(),
                "John",
                "Doe",
                "email@email.com",
                "password123"
        );
    }

    @Test
    void createUser() {
        // Arrange
        when(userRepositoryPort.save(userInformation)).thenReturn(Mono.just(userInformation));
        when(userRepositoryPort.existsByEmail(userInformation.getEmail())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(userInformation.getPassword())).thenReturn("encodedPassword");
        // Act
        Mono<UserInformation> result = userAdapter.createUser(Mono.just(userInformation));
        // Assert
        assertNotNull(result);
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(userInformation.getId(), user.getId());
                    assertEquals(userInformation.getFirstName(), user.getFirstName());
                    assertEquals(userInformation.getLastName(), user.getLastName());
                    assertEquals(userInformation.getEmail(), user.getEmail());
                    assertEquals("encodedPassword", user.getPassword());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void createUserFail() {
        // Arrange
        when(userRepositoryPort.existsByEmail(userInformation.getEmail())).thenReturn(Mono.just(true));
        // Act
        Mono<UserInformation> result = userAdapter.createUser(Mono.just(userInformation));
        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error->assertEquals(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS.getMessage().formatted(userInformation.getEmail()),error.getMessage()))
                .verify();

    }

    @Test
    void getUserById() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.getUserById(userId)).thenReturn(Mono.just(userInformation));
        // Act
        Mono<UserInformation> result = userAdapter.getUserById(userId, null, "Authorization");
        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(userInformation.getId(), user.getId());
                    assertEquals(userInformation.getFirstName(), user.getFirstName());
                    assertEquals(userInformation.getLastName(), user.getLastName());
                    assertEquals(userInformation.getEmail(), user.getEmail());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void getUserByIdWithAlbums() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String jwt="Authorization";
        when(userRepositoryPort.getUserById(userId)).thenReturn(Mono.just(userInformation));
        when(webClient.get()).thenReturn(getSpec);
        when(getSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.header(eq("Authorization"), eq(jwt))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Albums album1 = new Albums("album1",UUID.randomUUID(), "Summer Vacation");
        Albums album2 = new Albums("album2",UUID.randomUUID(), "Family Reunion");
        when(responseSpec.bodyToFlux(Albums.class)).thenReturn(Flux.just(album1, album2));
        // Act
        Mono<UserInformation> result = userAdapter.getUserById(userId, "Albums", jwt);
        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(userInformation.getId(), user.getId());
                    assertEquals(userInformation.getFirstName(), user.getFirstName());
                    assertEquals(userInformation.getLastName(), user.getLastName());
                    assertEquals(userInformation.getEmail(), user.getEmail());
                    assertEquals(2, user.getAlbums().size());
                    return true;
                })
                .verifyComplete();
    }
}