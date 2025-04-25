package com.ancas.reactive.ws.users.infrastructure.filter;

import com.ancas.reactive.ws.users.application.ports.IJwtPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JwtAuthenticationFilter implements WebFilter {
    private final IJwtPort jwtPort;
    private final List<String> publicPaths;

    public JwtAuthenticationFilter(IJwtPort jwtPort, List<String> publicPaths) {
        this.jwtPort = jwtPort;
        this.publicPaths = publicPaths;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().toString();
        if (isPublicPath(path, method)) {
            return chain.filter(exchange);
        }
        String token = extractToken(exchange);
        if (Objects.isNull(token)) {
            return handleUnauthorized(exchange);
        }
        return this.jwtPort.validateJwt(token)
                .flatMap(isValid->isValid?validAndContinue(token,exchange,chain):invalidToken(exchange));
    }

    private Mono<Void> validAndContinue(String token, ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.just(jwtPort.extractTokenSubject(token))
                .flatMap(subject->chain
                        .filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList())))
                );
    }

    private Mono<Void> invalidToken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private String extractToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return null;
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isPublicPath(String path, String method) {
        return publicPaths.stream()
                .anyMatch(entry -> {
                    String[] parts = entry.split(":", 2);
                    return parts.length == 2 &&
                            method.equalsIgnoreCase(parts[0]) &&
                            path.contains(parts[1]);
                });
    }
}
