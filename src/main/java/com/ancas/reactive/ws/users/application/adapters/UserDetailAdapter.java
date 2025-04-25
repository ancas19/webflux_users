package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
public class UserDetailAdapter implements ReactiveUserDetailsService {
    private final UserRepositoryPort userRepositoryPort;

    public UserDetailAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return this.userRepositoryPort.findByEmail(username)
                .map(user -> User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(new ArrayList<>())
                        .build()
                );
    }
}
