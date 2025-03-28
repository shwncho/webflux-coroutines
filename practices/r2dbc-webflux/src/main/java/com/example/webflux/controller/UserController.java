package com.example.webflux.controller;

import com.example.webflux.common.User;
import com.example.webflux.controller.dto.ProfileImageResponse;
import com.example.webflux.controller.dto.SignupUserRequest;
import com.example.webflux.controller.dto.UserResponse;
import com.example.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable String userId) {
        return ReactiveSecurityContextHolder
                .getContext()
                        .flatMap(context -> {
                            String name = context.getAuthentication().getName();

                            if(!name.equals(userId)) {
                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                            }

                            return userService.findById(userId)
                                    .map(this::map)
                                    .switchIfEmpty(
                                            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                                    );
                        });

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Mono<UserResponse> signupUser(
            @RequestBody SignupUserRequest request
    ) {
        return userService.createUser(request.getName(), request.getAge()
        , request.getPassword(), request.getProfileImageId())
                .map(this::map);

    }

    private UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getFollowCount(),
                user.getProfileImage().map(image ->
                        new ProfileImageResponse(
                                image.getId(),
                                image.getName(),
                                image.getUrl()
                        ))
        );
    }
}
