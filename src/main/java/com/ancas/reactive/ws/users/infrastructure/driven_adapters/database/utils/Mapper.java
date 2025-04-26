package com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.utils;

import com.ancas.reactive.ws.users.domain.models.UserInformation;
import com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.entities.UserEntity;

public class Mapper {
    public static UserEntity toEntity(com.ancas.reactive.ws.users.domain.models.UserInformation userInformation) {
        return new UserEntity(
                userInformation.getId(),
                userInformation.getFirstName(),
                userInformation.getLastName(),
                userInformation.getEmail(),
                userInformation.getPassword()
        );
    }

    public static UserInformation toResponse(UserEntity userInformation) {
        return new UserInformation(
                userInformation.getId(),
                userInformation.getFirstName(),
                userInformation.getLastName(),
                userInformation.getEmail(),
                userInformation.getPassword()
        );
    }
}
