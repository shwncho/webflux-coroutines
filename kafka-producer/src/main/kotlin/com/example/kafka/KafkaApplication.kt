package com.example.kafka

import com.example.kafka.produce.TestProducer
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class KafkaApplication(
	private val producer: TestProducer,
) : ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		runBlocking {
			repeat(1) { i ->
				producer.send("test", "test message")
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<KafkaApplication>(*args)
}
