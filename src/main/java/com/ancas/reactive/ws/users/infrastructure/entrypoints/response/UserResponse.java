package com.ancas.reactive.ws.users.infrastructure.entrypoints.response;

import com.ancas.reactive.ws.users.domain.models.Albums;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Albums> albums;

    public UserResponse() {
    }

    public UserResponse(UUID id, String firstName, String lastName, String email, List<Albums> albums) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.albums = albums;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public List<Albums> getAlbums() {
        return albums;
    }
    public void setAlbums(List<Albums> albums) {
        this.albums = albums;
    }
}
