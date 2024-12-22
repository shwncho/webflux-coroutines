package com.example.payment.application

import com.example.payment.domain.Order
import com.example.payment.domain.PgStatus
import com.example.payment.domain.ProductInOrder
import com.example.payment.exception.NoOrderFound
import com.example.payment.exception.NoProductFound
import com.example.payment.infrastructure.OrderRepository
import com.example.payment.infrastructure.ProductInOrderRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val productInOrderRepository: ProductInOrderRepository,
) {
    suspend fun create(request: ReqCreateOrder): Order {

        val prodIds = request.products.map { it.prodId }.toSet()
        val productsById = request.products.mapNotNull { productService.get(it.prodId) }.associateBy { it.id }
        prodIds.filter { !productsById.containsKey(it) }.let{ remains ->
            if(remains.isNotEmpty()){
                throw NoProductFound("prod ids: $remains")
            }
        }

        val amount = request.products.sumOf { productsById[it.prodId]!!.price * it.quantity }

        val description = request.products.joinToString(", ") { "${productsById[it.prodId]!!.name} x ${it.quantity}" }

        val newOrder = orderRepository.save(
            Order(
                userId = request.userId,
                description = description,
                amount = amount,
                pgOrderId = "${UUID.randomUUID()}".replace("-", ""),
                pgStatus = PgStatus.CREATE,
            )
        )

        request.products.forEach {
            productInOrderRepository.save(ProductInOrder(
                orderId = newOrder.id,
                prodId = it.prodId,
                price = productsById[it.prodId]!!.price,
                quantity = it.quantity,
            ))
        }

        return newOrder

    }

    suspend fun get(id: Long): Order {
        return orderRepository.findById(id) ?: throw NoOrderFound("orderId: $id")
    }

    suspend fun getAll(userId: Long): List<Order> {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
    }

    suspend fun delete(id: Long) {
        orderRepository.deleteById(id)
    }
}

class ReqCreateOrder(
    val userId: Long,
    var products: List<ReqProdQuantity>,
)

data class ReqProdQuantity(
    val prodId: Long,
    val quantity: Int,
)