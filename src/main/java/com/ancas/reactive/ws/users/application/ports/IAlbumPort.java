package com.ancas.reactive.ws.users.application.ports;

import com.ancas.reactive.ws.users.domain.models.Albums;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IAlbumPort {
    Mono<Albums> createAlbum(Mono<Albums> album, String jwt);
    Mono<Albums> updateAlbum(UUID albumId, Mono<Albums> album, String jwt);
    Mono<Void> deleteAlbum(String albumId, String jwt);
}
