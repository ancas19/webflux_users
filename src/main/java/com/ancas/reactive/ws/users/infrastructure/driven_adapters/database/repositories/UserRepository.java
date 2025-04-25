package com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.repositories;

import com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.entities.UserEntity;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
    Mono<Boolean> existsByEmail(String email);
    Flux<UserEntity> findAllBy(Pageable pageable);
    Mono<UserEntity> findByEmail(String username);
}
