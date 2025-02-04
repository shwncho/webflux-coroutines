package com.example.payment.infrastructure

import com.example.payment.domain.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {

    suspend fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Order>

    suspend fun findByPgOrderId(pgOrderId: String): Order?
}