package com.ancas.reactive.ws.users.domain.mapper;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.UserResponse;

public class UserMapper {
    public static UserResponse toResponse(UserInformation userInformation){
        return new UserResponse(
                userInformation.getId(),
                userInformation.getFirstName(),
                userInformation.getLastName(),
                userInformation.getEmail(),
                userInformation.getAlbums()
        );
    }

    public static UserInformation toUser(CreateUserRequest createUserRequest){
        return new UserInformation(
                createUserRequest.getId(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getPassword()
        );
    }
}
