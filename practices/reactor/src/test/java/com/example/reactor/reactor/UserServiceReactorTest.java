package com.example.reactor.reactor;

import com.example.reactor.common.User;
import com.example.reactor.reactor.future.UserReactorService;
import com.example.reactor.reactor.future.repository.ArticleReactorRepository;
import com.example.reactor.reactor.future.repository.FollowReactorRepository;
import com.example.reactor.reactor.future.repository.ImageReactorRepository;
import com.example.reactor.reactor.future.repository.UserReactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceReactorTest {

    UserReactorService userService;
    UserReactorRepository userRepository;
    ArticleReactorRepository articleRepository;
    ImageReactorRepository imageRepository;
    FollowReactorRepository followRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserReactorRepository();
        articleRepository = new ArticleReactorRepository();
        imageRepository = new ImageReactorRepository();
        followRepository = new FollowReactorRepository();

        userService = new UserReactorService(
                userRepository, articleRepository, imageRepository, followRepository
        );
    }

    @Test
    void getUserEmptyIfInvalidUserIdIsGiven() throws ExecutionException, InterruptedException {
        // given
        String userId = "invalid_user_id";

        // when
        Optional<User> user = userService.getUserById(userId).blockOptional();

        // then
        assertTrue(user.isEmpty());
    }

}
