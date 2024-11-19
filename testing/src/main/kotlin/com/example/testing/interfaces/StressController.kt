package com.example.testing.interfaces

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StressController {

    @GetMapping("/test/circuit/child/{flag}", "/test/circuit/child", "/test/circuit/child/")
    suspend fun testCircuitBreaker(@PathVariable flag: String?): String {
        if(flag?.lowercase() == "n")    return "success"
        else    throw RuntimeException("fail on child")
    }
}