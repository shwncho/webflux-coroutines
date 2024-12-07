package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.AccountService
import com.example.webfluxcoroutine.application.AdvancedService
import com.example.webfluxcoroutine.config.validator.DateString
import com.example.webfluxcoroutine.application.ExternalApi
import com.example.webfluxcoroutine.application.ResAccount
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvancedController(
    private val advancedService: AdvancedService,
    private val externalApi: ExternalApi,
    private val accountService: AccountService
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

    @GetMapping("/exteranl/delay")
    suspend fun delay() {
        externalApi.delay()
    }

    @GetMapping("/external/circuit/{flag}", "/external/circuit", "/external/circuit/" )
    suspend fun testCircuitBreaker(@PathVariable flag: String): String {
        return externalApi.testCircuitBreaker(flag)
    }

    @GetMapping("/account/{id}")
    suspend fun getAccount(@PathVariable id: Long): ResAccount {
        return accountService.get(id)
    }

    @PutMapping("/account/{id}/{amount}")
    suspend fun deposit(@PathVariable id: Long, @PathVariable amount: Long): ResAccount {
        accountService.addBalance(id, amount)
        return accountService.get(id)
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