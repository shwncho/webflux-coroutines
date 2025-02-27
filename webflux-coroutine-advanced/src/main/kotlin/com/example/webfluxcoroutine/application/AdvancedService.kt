package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class AdvancedService (
    private val repository: ArticleRepository,
){
    private val logger = KotlinLogging.logger {}

    suspend fun mdc1() {
        logger.debug { "start mdc 1" }
        mdc2()
        logger.debug { "end mdc 1" }
    }

    suspend fun mdc2() {
        logger.debug { "start mdc 2" }
        delay(100)
        repository.findById(1).let {
            logger.debug { "article: $it" }
        }

        Mono.fromCallable {
            logger.debug { "reactor call !!" }
        }.subscribeOn(Schedulers.boundedElastic()).awaitSingle()

        logger.debug { "end mdc 2" }
    }
}