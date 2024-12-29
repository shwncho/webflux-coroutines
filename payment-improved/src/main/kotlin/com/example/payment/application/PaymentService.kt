package com.example.payment.application

import com.example.payment.common.Beans.Companion.beanOrderService
import com.example.payment.domain.PgStatus
import com.example.payment.interfaces.ReqPayFailed
import com.example.payment.interfaces.ReqPaySucceed
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderService: OrderService,
    private val tossPayApi: TossPayApi,
) {

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgKey = request.paymentKey
            pgStatus = PgStatus.AUTH_SUCCESS
        }
        try {
            return if(order.amount != request.amount) {
                logger.error { "Invalid auth because of amount (order: ${order.amount}, pay: ${request.amount})" }
                order.pgStatus = PgStatus.AUTH_INVALID
                false
            } else {
                true
            }
        } finally {
            orderService.save(order)
        }
    }

    @Transactional
    suspend fun authFailed(request: ReqPayFailed) {
        val order = orderService.getOrderByPgOrderId(request.orderId)
        if(order.pgStatus == PgStatus.CREATE) {
            order.pgStatus == PgStatus.AUTH_FAIL
            orderService.save(order)
        }
        logger.error { """
            >> Fail on error
              - request: $request
              - order  : $order
        """.trimIndent() }
    }

    suspend fun capture(request: ReqPaySucceed): Boolean {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgStatus = PgStatus.CAPTURE_REQUEST
            beanOrderService.save(this)
        }
        logger.debug { ">> order: $order" }
        return try {
            tossPayApi.confirm(request).also { logger.debug { ">> res: $it" } }
            order.pgStatus = PgStatus.CAPTURE_SUCCESS
            true
        } catch (e: Exception) {
//            logger.error(e.message,e)
            order.pgStatus = when {
                e is WebClientRequestException -> PgStatus.CAPTURE_RETRY
                e is WebClientResponseException -> PgStatus.CAPTURE_FAIL
                else -> PgStatus.CAPTURE_FAIL
            }
            false
        } finally {
            orderService.save(order)
        }
    }
}