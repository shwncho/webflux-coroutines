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
)

fun main(args: Array<String>) {
	runApplication<KafkaApplication>(*args)
}
