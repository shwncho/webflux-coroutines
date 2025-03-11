package com.example.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/api/notifications")
@RestController
public class NotificationController {
    private static Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    private static AtomicInteger lastEventId = new AtomicInteger(1);

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getNotifications() {
        return sink.asFlux()
                .map(message -> {
                    String id = lastEventId.getAndIncrement() + "";
                    return ServerSentEvent
                            .builder(message)
                            .event("notification")
                            .id(id)
                            .comment("this is notification")
                            .build();
                });
    }

    @PostMapping
    public Mono<String> addNotification(@RequestBody Event event) {
        String notificationMessage = event.getType() + ": " + event.getMessage();
        sink.tryEmitNext(notificationMessage);

        return Mono.just("ok");
    }
}
