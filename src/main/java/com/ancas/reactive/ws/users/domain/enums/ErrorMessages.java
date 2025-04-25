package com.ancas.reactive.ws.users.domain.enums;

public enum ErrorMessages {

    ERROR_MESSAGE_EMAIL_ALREADY_EXISTS("That email(%s) already exists"),
    ERROR_MESSAGE_USER_NOT_FOUND("User not found with id: %s"),
    ERROR_MESSAGE_USER_NOT_FOUND_BY_EMAIL("User not found with email: %s"),
    ERROR_MESSAGE_USERS_NOT_FOUND("Users not found"),
    ERROR_MESSAGE_BAD_CREDENTIALS("Bad credentials, user or password is incorrected"), ;
    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
