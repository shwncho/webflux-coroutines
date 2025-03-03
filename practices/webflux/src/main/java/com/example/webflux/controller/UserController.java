package com.example.webflux.controller;

import com.example.webflux.controller.dto.UserResponse;
import com.example.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable String userId) {
        return userService.findById(userId)
                .map(user ->
                    new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getAge(),
                        user.getFollowCount()
                    )
                );
    }
}
