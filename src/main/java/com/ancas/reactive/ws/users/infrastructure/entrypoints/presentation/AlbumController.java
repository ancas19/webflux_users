package com.ancas.reactive.ws.users.infrastructure.entrypoints.presentation;

import com.ancas.reactive.ws.users.application.ports.IAlbumPort;
import com.ancas.reactive.ws.users.domain.mapper.AlbumMapper;
import com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.utils.Mapper;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.request.AlbumRequest;
import com.ancas.reactive.ws.users.infrastructure.entrypoints.response.AlbumResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/albums-client/albums")
public class AlbumController {
    private final IAlbumPort  albumPort;


    public AlbumController(IAlbumPort albumPort) {
        this.albumPort = albumPort;
    }

    @PostMapping
    public Mono<ResponseEntity<AlbumResponse>> createAlbum(
            @RequestBody Mono<AlbumRequest> albumRequest,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumRequest
                .map(AlbumMapper::toAlbum)
                .transform(album -> this.albumPort.createAlbum(album, jwt))
                .map(AlbumMapper::toAlbumResponse)
                .map(albumResponse -> ResponseEntity.status(HttpStatus.CREATED).body(albumResponse));
    }

    @PutMapping("/{albumId}")
    public Mono<ResponseEntity<AlbumResponse>> updateAlbum(
            @PathVariable UUID albumId,
            @RequestBody Mono<AlbumRequest> albumRequest,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumRequest
                .map(AlbumMapper::toAlbum)
                .transform(album -> this.albumPort.updateAlbum(albumId, album, jwt))
                .map(AlbumMapper::toAlbumResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{albumId}")
    public Mono<ResponseEntity<Void>> deleteAlbum(
            @PathVariable String albumId,
            @RequestHeader(name = "Authorization") String jwt) {
        return this.albumPort.deleteAlbum(albumId, jwt)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
