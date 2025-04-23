package com.ancas.reactive.ws.users.domain.mapper;

import com.ancas.reactive.ws.users.domain.models.User;
import com.ancas.reactive.ws.users.infrastructure.request.CreateUserRequest;
import com.ancas.reactive.ws.users.infrastructure.response.UserResponse;

public class UserMapper {
    public static UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static User toUser(CreateUserRequest createUserRequest){
        return new User(
                createUserRequest.getId(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getPassword()
        );
    }
}
