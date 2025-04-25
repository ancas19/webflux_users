package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IJwtPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtAdapter implements IJwtPort {
    @Value("${jwt.secret}")
    private String secretKey;
    @Override
    public String generateToken(String subject) {
        return Jwts
                .builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Mono<Boolean> validateJwt(String token) {
        return Mono.just(token)
                .map(this::parseToken)
                .map(claims -> claims.getExpiration().after(new Date()))
                .onErrorReturn(false);
    }

    @Override
    public String extractTokenSubject(String token) {
        return parseToken(token)
                .getSubject();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSigningKey(){
        return Optional.ofNullable(secretKey)
                .map(key->key.getBytes())
                .map(bytes-> Keys.hmacShaKeyFor(bytes))
                .orElseThrow(()->new IllegalArgumentException("Secret key is null"));
    }
}
