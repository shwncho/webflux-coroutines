package com.example.payment.infrastructure

import com.example.payment.domain.ProductInOrder
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductInOrderRepository: CoroutineCrudRepository<ProductInOrder, Long> {
    suspend fun countByOrderId(orderId: Long): Long
}