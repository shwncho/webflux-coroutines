package com.example.payment.application

import com.example.payment.common.Beans.Companion.beanOrderService
import com.example.payment.domain.Order
import com.example.payment.domain.PgStatus
import com.example.payment.domain.ProductInOrder
import com.example.payment.exception.NoOrderFound
import com.example.payment.exception.NoProductFound
import com.example.payment.infrastructure.OrderRepository
import com.example.payment.infrastructure.ProductInOrderRepository
import com.example.payment.interfaces.ReqPayFailed
import com.example.payment.interfaces.ReqPaySucceed
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val productInOrderRepository: ProductInOrderRepository,
    private val tossPayApi: TossPayApi,
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun save(order: Order) {
        orderRepository.save(order)
    }

    suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?:
            throw NoOrderFound("pgOrderId: $pgOrderId")
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