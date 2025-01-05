package com.example.kafka

import com.example.kafka.config.Consumer
import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableKafka
class KafkaApplication(
	private val consumer: Consumer,
): ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		consumer.consume("test", "A") {
			logger.debug { ">> Received message: $it" }
		}
		logger.debug { ">> ready consumer" }
	}
}

fun main(args: Array<String>) {
	runApplication<KafkaApplication>(*args)
}
