package com.example.payment.application

import com.example.payment.domain.Order
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

@Service
class CaptureMarker(
    private val template: ReactiveRedisTemplate<Any, Any>,
    @Value("\${spring.profiles.active:local}")
    private val profile: String,
) {

    private val ops = template.opsForHash<Long, Order>()
    private val key = "$profile/capture-marker"

    suspend fun put(order: Order) {
        ops.put(key, order.id, order).awaitFirstOrNull()
    }

    suspend fun remove(order: Order) {
        ops.remove(key, order.id).awaitFirstOrNull()
    }

    suspend fun getAll(): List<Order> {
        return ops.values(key).asFlow().toList().sortedBy { it.updatedAt }
    }
}