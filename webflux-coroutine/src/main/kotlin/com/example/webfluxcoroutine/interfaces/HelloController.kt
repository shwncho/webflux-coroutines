package com.example.webfluxcoroutine.interfaces

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    suspend fun index(): String = "main page"

    @GetMapping("/hello")
    suspend fun hello(): String = "Hello World"
}