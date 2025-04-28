package com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.adapters;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.repositories.UserRepository;
import com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.utils.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserRepository userRepository;

    public UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<UserInformation> save(UserInformation user) {
        return this.userRepository.save(Mapper.toEntity(user))
                .map(Mapper::toResponse);
    }

    @Override
    public Mono<UserInformation> getUserById(UUID userId) {
        return this.userRepository.findById(userId)
                .map(Mapper::toResponse);
    }

    @Override
    public Flux<UserInformation> getAllUsers(int page, int size) {
        return this.userRepository.findAllBy(PageRequest.of(page, size))
                .map(Mapper::toResponse);
    }

    @Override
    public Mono<UserInformation> findByEmail(String username) {
        return this.userRepository.findByEmail(username)
                .map(Mapper::toResponse);
    }

    @Override
    public Mono<Boolean> existsById(String userId) {
        return this.userRepository.existsById(UUID.fromString(userId));
    }
}
