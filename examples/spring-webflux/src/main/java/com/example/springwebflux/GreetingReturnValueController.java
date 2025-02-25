package com.example.springwebflux;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/return-value")
@Controller
public class GreetingReturnValueController {
    @ResponseBody
    @GetMapping("/future")
    CompletableFuture<String> helloAsFuture() {
        return CompletableFuture.completedFuture("Hello World");
    }

    @GetMapping("/void-shr")
    Mono<Void> monoVoid(ServerHttpResponse response) {
        return response.writeWith(
                Mono.just(response.bufferFactory()
                        .wrap("hello world".getBytes()))
        );
    }

    @GetMapping("/void-swe")
    Mono<Void> monoVoidEx(ServerWebExchange exchange) {
        return exchange.getResponse().setComplete();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/void-status")
    Void voidStatus() {
        return null;
    }

    @ResponseBody
    @GetMapping("/void")
    Void voidEmptyResp() {
        return null;
    }
}
