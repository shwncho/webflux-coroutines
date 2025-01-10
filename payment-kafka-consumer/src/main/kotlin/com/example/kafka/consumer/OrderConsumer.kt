package com.example.kafka.consumer

import com.example.kafka.config.Consumer
import org.springframework.context.annotation.Configuration

@Configuration
class OrderConsumer(
    private val consumer: Consumer,
    private val historyApi: HistoryApi,
)