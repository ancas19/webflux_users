package com.ancas.reactive.ws.users.application.adapters;

import com.ancas.reactive.ws.users.application.ports.IAlbumPort;
import com.ancas.reactive.ws.users.domain.enums.ErrorMessages;
import com.ancas.reactive.ws.users.domain.exception.BadRequestException;
import com.ancas.reactive.ws.users.domain.exception.NotFoundException;
import com.ancas.reactive.ws.users.domain.models.Albums;
import com.ancas.reactive.ws.users.domain.ports.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AlbumsAdapter implements IAlbumPort {
    private static Logger log= LoggerFactory.getLogger(AlbumsAdapter.class);
    private final WebClient webClient;
    private final UserRepositoryPort userRepositoryPort;

    public AlbumsAdapter(WebClient webClient, UserRepositoryPort userRepositoryPort) {
        this.webClient = webClient;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Mono<Albums> createAlbum(Mono<Albums> albumRequest, String jwt) {
        return albumRequest
                .zipWhen(album -> userRepositoryPort.existsById(album.getUserId())
                        .flatMap(value->value?Mono.just(true):Mono.error(new NotFoundException(ErrorMessages.ERROR_MESSAGE_USER_NOT_FOUND.getMessage().formatted(album.getUserId()))))
                )
                .flatMap(tuple->this.callEndpointToCreateAlbum(tuple.getT1(),jwt));
    }

    @Override
    public Mono<Albums> updateAlbum(UUID albumId, Mono<Albums> albumRequest, String jwt) {
        return albumRequest
                .zipWhen(album -> userRepositoryPort.existsById(album.getUserId())
                        .flatMap(value->value?Mono.just(true):Mono.error(new NotFoundException(ErrorMessages.ERROR_MESSAGE_USER_NOT_FOUND.getMessage().formatted(album.getUserId()))))
                )
                .flatMap(tuple->this.callEndpointToUpdateAlbum(tuple.getT1(),albumId,jwt));
    }


    @Override
    public Mono<Void> deleteAlbum(String albumId, String jwt) {
        return webClient.delete()
                .uri("/albums/%s".formatted(albumId))
                .header("Authorization", jwt)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(error->{
                    log.error("Error deleting album: {}", error.getMessage());
                    return Mono.error(new BadRequestException(ErrorMessages.ERROR_MESSAGE_ALBUM_DELETION_FAILED.getMessage().formatted(albumId)));
                });
    }

    private Mono<Albums> callEndpointToCreateAlbum(Albums album, String jwt) {
        return webClient.post()
                .uri("/albums")
                .bodyValue(album)
                .header("Authorization", jwt)
                .retrieve()
                .bodyToMono(Albums.class)
                .onErrorResume(error->{
                    log.error("Error creating album: {}", error.getMessage());
                    return Mono.error(new BadRequestException(ErrorMessages.ERROR_MESSAGE_ALBUM_CREATION_FAILED.getMessage().formatted(album.getTitle())));
                });
    }

    private Mono<Albums> callEndpointToUpdateAlbum(Albums t1, UUID albumId, String jwt) {
        t1.setId(albumId);
        return webClient.put()
                .uri("/albums/%s".formatted(albumId))
                .bodyValue(t1)
                .header("Authorization", jwt)
                .retrieve()
                .bodyToMono(Albums.class)
                .onErrorResume(error->{
                    log.error("Error updating album: {}", error.getMessage());
                    return Mono.error(new BadRequestException(ErrorMessages.ERROR_MESSAGE_ALBUM_UPDATE_FAILED.getMessage().formatted(t1.getTitle())));
                });
    }
}
