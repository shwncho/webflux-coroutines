package com.example.payment.application.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class PaymentApi(
    @Value("\${payment.self.domain}")
    private val domain: String
) {
    private val client = WebClient.builder().baseUrl(domain)
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()

    suspend fun recapture(orderId: Long) {
        client.put().uri("/orders/recapture/$orderId").retrieve()
    }
}