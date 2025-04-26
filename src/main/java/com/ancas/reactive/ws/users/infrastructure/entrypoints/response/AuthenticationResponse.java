package com.ancas.reactive.ws.users.infrastructure.entrypoints.response;

import java.util.UUID;

public class AuthenticationResponse {
    private UUID userId;
    private String token;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(UUID userId,String token) {
        this.userId = userId;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
