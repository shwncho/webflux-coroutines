package com.example.payment.application

import com.example.payment.config.CacheKey
import com.example.payment.config.CacheManager
import com.example.payment.domain.Product
import com.example.payment.infrastructure.ProductRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.seconds

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val cacheManager: CacheManager,
    @Value("\${spring.config.activate.on-profile:local}")
    private val profile: String,
) {

    val CACHE_KEY = "${profile}/payment/product".also { cacheManager.ttl[it] = 10.seconds }
    suspend fun get(id: Long): Product? {
        val key = CacheKey(CACHE_KEY, id)
        return cacheManager.get(key) {
            productRepository.findById(id)
        }
    }
}