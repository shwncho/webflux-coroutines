package com.example.webfluxcoroutine.interfaces

import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisPubSubController(
    private val template: ReactiveRedisTemplate<Any,Any>,
): ApplicationListener<ApplicationReadyEvent> {

    private val logger = KotlinLogging.logger {}

    @PostMapping("/send/{message}")
    suspend fun pub(@PathVariable("message") message: String) {
        template.convertAndSend("test-topic", message).awaitSingle()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        template.listenToChannel("test-topic").doOnNext{
            logger.debug { ">> Received: $it" }
        }.subscribe()
    }
}