package com.ancas.reactive.ws.users.infrastructure.entrypoints.request;

import java.util.UUID;

public class AlbumRequest {
    private String userId;
    private UUID id;
    private String title;

    public AlbumRequest() {
    }
    public AlbumRequest(String userId, UUID id, String title) {
        this.userId = userId;
        this.id = id;
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
