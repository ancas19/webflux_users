package com.ancas.reactive.ws.users.domain.enums;

public enum ErrorMessages {

    ERROR_MESSAGE_EMAIL_ALREADY_EXISTS("That email(%s) already exists"),
    ERROR_MESSAGE_USER_NOT_FOUND("User not found with id: %s"),
    ERROR_MESSAGE_USERS_NOT_FOUND("Users not found"),;
    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
