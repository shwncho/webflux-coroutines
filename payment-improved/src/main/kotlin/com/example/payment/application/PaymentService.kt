package com.example.payment.application

import com.example.payment.application.api.PaymentApi
import com.example.payment.application.api.TossPayApi
import com.example.payment.common.Beans
import com.example.payment.domain.Order
import com.example.payment.domain.PgStatus.*
import com.example.payment.exception.InvalidOrderStatus
import com.example.payment.interfaces.ReqPayFailed
import com.example.payment.interfaces.ReqPaySucceed
import com.example.payment.interfaces.TossPaymentType
import com.fasterxml.jackson.databind.ObjectMapper
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
    private val objectMapper: ObjectMapper,
    private val paymentApi: PaymentApi,
) {

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgKey = request.paymentKey
            pgStatus = AUTH_SUCCESS
        }
        try {
            return if(order.amount != request.amount) {
                logger.error { "Invalid auth because of amount (order: ${order.amount}, pay: ${request.amount})" }
                order.pgStatus = AUTH_INVALID
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
        if(order.pgStatus == CREATE) {
            order.pgStatus == AUTH_FAIL
            orderService.save(order)
        }
        logger.error { """
            >> Fail on error
              - request: $request
              - order  : $order
        """.trimIndent() }
    }


    //최초로 결제 요청을 받고 수행하는 캡쳐부분
    @Transactional
    suspend fun capture(request: ReqPaySucceed) {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgStatus = CAPTURE_REQUEST
            Beans.beanOrderService.save(this)
        }
        capture(order)
    }

    //요청받은 결제에 대해 그 뒤 로직을 담당하는 캡쳐부분
    @Transactional
    suspend fun capture(order: Order) {
        logger.debug { ">> order: $order" }
        if(order.pgStatus !in setOf(CAPTURE_REQUEST, CAPTURE_RETRY))
            throw InvalidOrderStatus("invalid order status (orderId: ${order.id}, status: ${order.pgStatus}")
        order.increaseRetryCount()
        try {
            tossPayApi.confirm(order.toReqPaySucceed()).also { logger.debug { ">> res: $it" } }
            order.pgStatus = CAPTURE_SUCCESS
        } catch (e: Exception) {
//            logger.error(e.message,e)
            order.pgStatus = when (e) {
                is WebClientRequestException -> CAPTURE_RETRY
                is WebClientResponseException -> {
                    val resError = e.toTossPayApiError()
                    logger.debug { ">> res error: $resError" }
                    when(resError.code) {
                        "ALREADY_PROCESSED_PAYMENT" -> CAPTURE_SUCCESS
                        "PROVIDER_ERROR", "FAILED_INTERNAL_SYSTEM_PROCESSING" -> CAPTURE_RETRY
                        else -> CAPTURE_FAIL
                    }
                    CAPTURE_FAIL
                }
                else -> CAPTURE_FAIL
            }
            if(order.pgStatus == CAPTURE_RETRY && order.pgRetryCount >= 3) {
                order.pgStatus == CAPTURE_FAIL
            }
            if(order.pgStatus == CAPTURE_SUCCESS) throw e
        } finally {
            orderService.save(order)
            if(order.pgStatus == CAPTURE_RETRY) {
                paymentApi.recapture(order.id)
            }
        }
    }

    private fun Order.toReqPaySucceed(): ReqPaySucceed {
        return this.let {
            ReqPaySucceed(
                paymentKey = it.pgKey!!,
                orderId = it.pgOrderId!!,
                amount = it.amount,
                paymentType = TossPaymentType.NORMAL,
            )
        }
    }

    private fun WebClientResponseException.toTossPayApiError(): TossPayApiError {
        val json = String(this.responseBodyAsByteArray)
        return objectMapper.readValue(json, TossPayApiError::class.java)
    }
}

data class TossPayApiError(
    val code: String,
    val message: String,
)