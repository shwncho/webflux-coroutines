package com.example.springwebflux.sse;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequestMapping("/sse")
@RestController
public class SseController {
    @GetMapping(path = "/simple", produces = "test/event-stream")
    Flux<String> simpleSse() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> "Hello " + i);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<String>> sse(
            @RequestHeader(name = "Last-Event-ID",
                required = false, defaultValue = "0") Long lastEventId
    ) {
        return Flux.range(0,5)
                .delayElements(Duration.ofMillis(100))
                .map(i -> ServerSentEvent.<String>builder()
                        .event("add")
                        .id(String.valueOf(i + lastEventId + 1))
                        .data("data-" + i)
                        .comment("comment-" +i)
                        .build()
                );
    }
}
