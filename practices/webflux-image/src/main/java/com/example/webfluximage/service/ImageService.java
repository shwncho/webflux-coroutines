package com.example.webfluximage.service;

import com.example.webfluximage.entity.common.Image;
import com.example.webfluximage.entity.common.repository.ImageEntity;
import com.example.webfluximage.repository.ImageReactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageReactorRepository imageRepository;

    public Mono<Image> getImageById(String imageId) {
        return imageRepository.findById(imageId)
                .map(this::map);
    }

    public Mono<Image> createImage(String id, String name, String url) {
        return imageRepository.create(id, name, url)
                .map(this::map);
    }

    private Image map(ImageEntity imageEntity) {
        return new Image(
                imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()
        );
    }
}
