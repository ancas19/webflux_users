package com.ancas.reactive.ws.users.domain.enums;

public enum Constants {

    NOT_FOUND("NOT FOUND"),
    BAD_REQUEST("BAD REQUEST"),
    DATA_ERROR("DATA ERROR"),
    INTERNAL_SERVER_ERROR("INTERNAL SERVER ERROR"),
    UNAUTHENTICATED("UNAUTHENTICATED"),
    ACCESS_DENIED("ACCESS DENIED"),;
    private String value;
    private Constants(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
