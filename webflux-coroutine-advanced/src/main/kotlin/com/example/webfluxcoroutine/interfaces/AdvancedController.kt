package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.AdvancedService
import com.example.webfluxcoroutine.config.validator.DateString
import com.example.webfluxcoroutine.exception.InvalidParameter
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.jetbrains.annotations.NotNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PutMapping("/test/error")
    suspend fun error(@RequestBody @Valid request: ReqErrorTest) {
        if(request.message == "error") {
            throw InvalidParameter(request, request::message,code = "custom code", message = "custom error")
        }
    }
}

data class ReqErrorTest (
    @field:NotEmpty
    @field:Size(min=3, max = 10)
    val id: String?,
    @field:NotNull
    @field:Positive(message = "양수만 입력 가능")
    @field:Max(100)
    val age: Int?,
    @field:DateString
    val birthday: String?,

    val message: String? = null

)