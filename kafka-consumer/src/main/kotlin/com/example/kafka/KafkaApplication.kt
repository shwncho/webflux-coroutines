package com.example.kafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class KafkaApplication

fun main(args: Array<String>) {
	runApplication<KafkaApplication>(*args)
}
