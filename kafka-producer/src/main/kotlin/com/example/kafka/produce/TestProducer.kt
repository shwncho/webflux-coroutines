package com.example.kafka.produce

import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class TestProducer(
    private val template: KafkaTemplate<String, String>
) {
    fun send(topic: String, message: String) {
        logger.debug { "Sending $topic to $message" }
        template.send(topic, message)
    }
}