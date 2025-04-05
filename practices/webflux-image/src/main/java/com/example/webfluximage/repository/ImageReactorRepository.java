package com.example.webfluximage.repository;

import com.example.webfluximage.entity.common.repository.ImageEntity;
import com.example.webfluximage.entity.common.repository.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ImageReactorRepository {
    private final ReactiveHashOperations<String,String,String> hashOperations;

    public ImageReactorRepository(
            ReactiveStringRedisTemplate redisTemplate
    ) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @SneakyThrows
    public Mono<ImageEntity> findById(String id) {
        return Mono.create(sink -> {
            log.info("ImageRepository.findById: {}", id);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            hashOperations.multiGet(id, List.of("id", "name", "url"))
                    .subscribe(strings -> {
                        log.info("strings: {}", strings);
                        if (strings.stream().allMatch(item -> item == null)) {
                            sink.error(new RuntimeException("image not found"));
                            return;
                        }
                        var image = new ImageEntity(
                                strings.get(0),
                                strings.get(1),
                                strings.get(2)
                        );

                        sink.success(image);
                    });
        });
    }

    public Mono<ImageEntity> findWithContext() {
        return Mono.deferContextual(context -> {
            Optional<UserEntity> userOptional = context.getOrEmpty("user");
            if(userOptional.isEmpty()) throw new RuntimeException("user not found");

            return Mono.just(userOptional.get().getProfileImageId());
        }).flatMap(this::findById);
    }
}
