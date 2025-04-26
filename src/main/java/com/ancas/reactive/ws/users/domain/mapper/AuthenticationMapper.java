package com.ancas.reactive.ws.users.domain.mapper;

import com.ancas.reactive.ws.users.domain.models.Authentication;
import com.ancas.reactive.ws.users.domain.models.Token;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.AuthenticationRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.AuthenticationResponse;

public class AuthenticationMapper {

    public static Authentication toAuthentication(AuthenticationRequest request) {
        return new Authentication(request.getEmail(), request.getPassword());
    }

    public static AuthenticationResponse toAuthenticationResponse(Token token) {
        return new AuthenticationResponse(token.getUserId(), token.getToken());
    }
}
