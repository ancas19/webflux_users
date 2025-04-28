package com.ancas.reactive.ws.users.domain.mapper;

import com.ancas.reactive.ws.users.domain.models.Albums;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.AlbumRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.AlbumResponse;

public class AlbumMapper {
    public static AlbumResponse toAlbumResponse(Albums album) {
        return new AlbumResponse(album.getUserId(),album.getId(), album.getTitle());
    }

    public static Albums toAlbum(AlbumRequest request) {
        return new Albums(request.getUserId(), request.getId(), request.getTitle());
    }
}
