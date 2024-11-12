package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.AdvancedService
import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvancedController(
    private val advancedService: AdvancedService,
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping("/test/mdc")
    suspend fun mdc(){
        withContext(MDCContext()){
            logger.debug { "start MDC TxId" }
            delay(100)
            advancedService.mdc1()
            logger.debug { "end MDC TxId" }
        }
    }
}