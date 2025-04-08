package com.example.sse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStreamOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Slf4j
@Service
public class NotificationService {
    private static final String STREAM_NAME = "notification:1";
    private ReactiveStreamOperations<String, String, String> streamOperations;
    private static Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    public NotificationService(ReactiveStringRedisTemplate redisTemplate) {
        streamOperations = redisTemplate.opsForStream();
    }

    public Flux<String> getMessageFromSink() {
        return sink.asFlux();
    }

    public void tryEmitNext(String message) {
        streamOperations.add(STREAM_NAME, Map.of("message", message));
        log.info("message: {}", message);
    }
}
