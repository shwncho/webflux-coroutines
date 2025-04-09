package com.example.sse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveStreamOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {
    private static final String STREAM_NAME = "notification:1";
    private ReactiveStreamOperations<String, String, String> streamOperations;
    private StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;
    private static Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    public NotificationService(
            ReactiveStringRedisTemplate redisTemplate,
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory
    ) {
        streamOperations = redisTemplate.opsForStream();
        var options = StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();
        streamReceiver = StreamReceiver.create(reactiveRedisConnectionFactory, options);

        var streamOffset = StreamOffset.create(STREAM_NAME, ReadOffset.latest());
        streamReceiver.receive(streamOffset)
                .subscribe(record -> {
                    var values = record.getValue();
                    var message = values.get("message");
                    sink.tryEmitNext(message);
                });
    }

    public Flux<String> getMessageFromSink() {
        return sink.asFlux();
    }

    public void tryEmitNext(String message) {
        streamOperations.add(STREAM_NAME, Map.of("message", message))
                .subscribe();
    }
}
