package com.example.payment.interfaces

import com.example.payment.application.CaptureMarker
import com.example.payment.application.OrderHistoryService
import com.example.payment.application.OrderService
import com.example.payment.application.PaymentService
import com.example.payment.application.QryOrderHistory
import com.example.payment.application.ReqCreateOrder
import com.example.payment.common.Beans.Companion.beanProductInOrderRepository
import com.example.payment.common.Beans.Companion.beanProductService
import com.example.payment.domain.Order
import com.example.payment.domain.PgStatus
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderHistoryService: OrderHistoryService,
    private val paymentService: PaymentService,
    private val captureMarker: CaptureMarker,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): ResOrder {
        return orderService.get(id).toResOrder()
    }

    @GetMapping("/all/{userId}")
    suspend fun getAll(@PathVariable userId: Long): List<ResOrder> {
        return orderService.getAll(userId).map { it.toResOrder() }
    }

    @PostMapping
    suspend fun create(@RequestBody request: ReqCreateOrder): ResOrder {
        return orderService.create(request).toResOrder()
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
        orderService.delete(id)
    }

    @GetMapping("/history")
    suspend fun getHistories(request: QryOrderHistory): List<Order> {
        return orderHistoryService.getHistories(request)
    }

    @PutMapping("/recapture/{id}")
    suspend fun recapture(@PathVariable id: Long) {
        orderService.get(id)?.let { order ->
            logger.debug { ">> recapture: $order" }
            delay(getBackOffDelay(order).also { logger.debug { ">> delay: $it ms" } })
            // temp = 2 ^ retry count
            // delay = (temp / 2) * (0..(temp/2)).random
            paymentService.capture(order)
        }
    }

    private fun getBackOffDelay(order: Order): Duration {
        val temp = (2.0).pow(order.pgRetryCount).toInt() * 1000
        val delay = temp + (0..temp).random()
        return delay.milliseconds
    }

    @GetMapping("/capturing")
    suspend fun getCapturingOrder(): List<Order> {
        return captureMarker.getAll()
    }
}

suspend fun Order.toResOrder(): ResOrder {
    return this.let{
        ResOrder(
            id = it.id,
            userId = it.userId,
            description = it.description,
            amount = it.amount,
            pgOrderId = it.pgOrderId,
            pgKey = it.pgKey,
            pgStatus = it.pgStatus,
            pgRetryCount = it.pgRetryCount,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            products = beanProductInOrderRepository.findAllByOrderId(it.id).map { prodInOrd ->
                ResProductQuantity(
                    id = prodInOrd.prodId,
                    name = beanProductService.get(prodInOrd.prodId)?.name ?: "unknown",
                    price = prodInOrd.price,
                    quantity = prodInOrd.quantity,
                )
            },
        )
    }
}

data class ResOrder(
    val id: Long,
    val userId: Long,
    val description: String? = null,
    val amount: Long,
    val pgOrderId: String? = null,
    val pgKey: String? = null,
    val pgStatus: PgStatus = PgStatus.CREATE,
    val pgRetryCount: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val products: List<ResProductQuantity>
)

data class ResProductQuantity(
    val id: Long,
    val name: String,
    val price: Long,
    val quantity: Int,
)