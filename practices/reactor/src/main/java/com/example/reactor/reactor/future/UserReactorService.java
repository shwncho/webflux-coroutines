package com.example.reactor.reactor.future;

import com.example.reactor.common.Article;
import com.example.reactor.common.Image;
import com.example.reactor.common.User;
import com.example.reactor.common.repository.UserEntity;
import com.example.reactor.reactor.future.repository.ArticleReactorRepository;
import com.example.reactor.reactor.future.repository.FollowReactorRepository;
import com.example.reactor.reactor.future.repository.ImageReactorRepository;
import com.example.reactor.reactor.future.repository.UserReactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class UserReactorService {
    private final UserReactorRepository userRepository;
    private final ArticleReactorRepository articleRepository;
    private final ImageReactorRepository imageRepository;
    private final FollowReactorRepository followRepository;

    @SneakyThrows
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .flatMap(userEntity -> {
                    return Mono.fromFuture(this.getUser(Optional.of(userEntity)));
                }).map(Optional::get);
    }

    @SneakyThrows
    private CompletableFuture<Optional<User>> getUser(Optional<UserEntity> userEntityOptional) {
        if(userEntityOptional.isEmpty())    
            return CompletableFuture.completedFuture(Optional.empty());

        var userEntity = userEntityOptional.get();

        var imageFuture = imageRepository.findById(userEntity.getProfileImageId())
                .thenApplyAsync(imageEntityOptional ->
                        imageEntityOptional.map(imageEntity ->
                            new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl())
                        )
                );

        var articlesFuture = articleRepository.findAllByUserId(userEntity.getId())
                .thenApplyAsync( articleEntities ->
                    articleEntities.stream().
                            map(articleEntity ->
                                new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent())
                            )
                        .collect(Collectors.toList())
                );

        var followCountFuture = followRepository.countByUserId(userEntity.getId());

        return CompletableFuture.allOf(imageFuture, articlesFuture, followCountFuture)
                .thenAcceptAsync(v -> {
                    log.info("Three futures are completed");
                })
                .thenRunAsync(() -> {
                    log.info("Three futures are also completed");
                })
                .thenApplyAsync(v -> {
                    try {
                        var image = imageFuture.get();
                        var articles = articlesFuture.get();
                        var followCount = followCountFuture.get();

                        return Optional.of(
                                new User(
                                        userEntity.getId(),
                                        userEntity.getName(),
                                        userEntity.getAge(),
                                        image,
                                        articles,
                                        followCount
                                )
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
