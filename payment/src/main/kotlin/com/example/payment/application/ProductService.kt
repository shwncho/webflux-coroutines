package com.example.payment.application

import com.example.payment.domain.Product
import com.example.payment.infrastructure.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    suspend fun get(id: Long): Product? {
        return productRepository.findById(id)
    }
}