package com.example.webfluxreactor.interfaces

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloController {

    @GetMapping("/")
    fun health(): Mono<String> = Mono.just("Hello WebFlux")
}