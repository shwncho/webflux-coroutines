package com.example.payment.application

import com.example.payment.common.KafkaProducer
import com.example.payment.domain.Order
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

val TOPIC_PAYMENT = "payment"

@Service
class KafkaPipeline(
    private val producer: KafkaProducer,
    private val mapper: ObjectMapper,
) {
    suspend fun sendPayment(order: Order) {
        mapper.writeValueAsString(order).let { json ->
            producer.send(TOPIC_PAYMENT, json)
        }
    }
}