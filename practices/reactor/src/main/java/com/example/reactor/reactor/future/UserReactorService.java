package com.example.reactor.reactor.future;

import com.example.reactor.common.Article;
import com.example.reactor.common.EmptyImage;
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
import reactor.util.context.Context;

import java.util.List;
import java.util.Optional;

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
                .flatMap(this::getUser);
    }

    @SneakyThrows
    private Mono<User> getUser(UserEntity userEntity) {
        Context context = Context.of("user", userEntity);
        var imageMono = imageRepository.findWithContext()
                .map(imageEntity ->
                        new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl())
                ).onErrorReturn(new EmptyImage())
                .contextWrite(context);


        var articlesMono = articleRepository.findAllWithContext()
                .skip(5)
                .take(2)
                .map(articleEntity ->
                            new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent())
                ).collectList().doOnSubscribe(subscription -> {
                    log.info("subscribe articleRepository");
                }).contextWrite(context);

        var followCountMono = followRepository.countWithContext()
                .contextWrite(context);


        return Mono.zip(imageMono, articlesMono, followCountMono)
                .map(resultTuple -> {
                    Image image = resultTuple.getT1();
                    List<Article> articles = resultTuple.getT2();
                    Long followCount = resultTuple.getT3();

                    Optional<Image> imageOptional = Optional.empty();
                    if (!(image instanceof EmptyImage)) {
                        imageOptional = Optional.of(image);
                    }

                    return new User(
                            userEntity.getId(),
                            userEntity.getName(),
                            userEntity.getAge(),
                            imageOptional,
                            articles,
                            followCount
                    );
                });
    }
}
