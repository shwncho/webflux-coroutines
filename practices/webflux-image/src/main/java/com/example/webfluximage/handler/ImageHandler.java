package com.example.webfluximage.handler;

import com.example.webfluximage.handler.dto.ImageResponse;
import com.example.webfluximage.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ImageHandler {

    private final ImageService imageService;

    public Mono<ServerResponse> getImageById(ServerRequest request) {
        String imageId = request.pathVariable("imageId");

        return imageService.getImageById(imageId)
                .map(image ->
                        new ImageResponse(image.getId(), image.getName(), image.getUrl())
                ).flatMap(imageResponse ->
                        ServerResponse.ok().bodyValue(imageResponse)
                ).onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()));
    }
}
