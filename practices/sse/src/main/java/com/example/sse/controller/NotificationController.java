package com.example.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequestMapping("/api/notifications")
@RestController
public class NotificationController {
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getNotifications() {
        return Flux.interval(Duration.ofMillis(1000))
                .map(v -> "hello world");
    }
}
