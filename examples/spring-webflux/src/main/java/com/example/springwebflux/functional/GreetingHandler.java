package com.example.springwebflux.functional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class GreetingHandler {
    public static Mono<ServerResponse> greetQueryParam(ServerRequest request) {
        String name = request.queryParam("name")
                .orElse("world");

        String content = "Hello, " + name + "!";
        return ServerResponse.ok().bodyValue(content);
    }

    public static Mono<ServerResponse> greetPathVariable(ServerRequest request) {
        String name = request.pathVariable("name");

        String content = "Hello, " + name + "!";
        return ServerResponse.ok().bodyValue(content);
    }

    public static Mono<ServerResponse> greetJsonBody(ServerRequest request) {
        return request.bodyToMono(NameHolder.class)
                .map(NameHolder::getName)
                .map(name -> "Hello " + name)
                .flatMap(content -> ServerResponse.ok().bodyValue(content))
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    public static Mono<ServerResponse> greetPlainTextBody(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(name -> "Hello " + name)
                .flatMap(content -> ServerResponse.ok().bodyValue(content));
    }

    public static Mono<ServerResponse> greetHeader(ServerRequest request) {
        String name = request.headers().header("X-Custom-Name")
                .stream()
                .findFirst()
                .orElse("world");

        String content = "Hello " + name;
        return ServerResponse.ok().bodyValue(content);
    }
}
