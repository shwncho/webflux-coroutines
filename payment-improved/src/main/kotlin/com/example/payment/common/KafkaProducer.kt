package com.example.payment.common

import com.example.payment.domain.Order
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions

import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

@Service
class KafkaProducer(
    private val template: ReactiveKafkaProducerTemplate<String, String>,
) {
    suspend fun send(topic: String, message: String) {
        logger.debug { ">> send to [$topic]: $message" }
        template.send(topic,message).awaitSingle()
    }
}

@Configuration
class ReactiveKafkaInitializer{
    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, String> {
        return properties.buildProducerProperties(null)
            .let { prop ->
                prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
                SenderOptions.create<String,String>(prop) }
            .let { option -> ReactiveKafkaProducerTemplate(option) }
    }
}