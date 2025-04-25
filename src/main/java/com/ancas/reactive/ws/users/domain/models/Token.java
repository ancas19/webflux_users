package com.ancas.reactive.ws.users.domain.models;

import java.util.UUID;

public class Token {
    private UUID userId;
    private String token;

    public Token() {
    }

    public Token(UUID userId,String token) {
        this.token = token;
        this.userId = userId;
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
