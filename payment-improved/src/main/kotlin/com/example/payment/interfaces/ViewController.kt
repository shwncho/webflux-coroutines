package com.example.payment.interfaces

import com.example.payment.application.OrderService
import com.example.payment.application.PaymentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewController(
    private val orderService: OrderService,
    private val paymentService: PaymentService,
) {

    @GetMapping("/hello/{name}")
    suspend fun hello(@PathVariable name: String, model: Model): String {
        model.addAttribute("pname", name)
        model.addAttribute("order", orderService.get(3).toResOrder())
        return "hello-world.html"
    }

    @GetMapping("/pay/{orderId}")
    suspend fun pay(@PathVariable orderId: Long, model: Model): String {
        model.addAttribute("order", orderService.get(orderId))
        return "pay.html"
    }

    @GetMapping("/pay/success")
    suspend fun paySucceed(request: ReqPaySucceed): String {
        if(!paymentService.authSucceed(request))
            return "pay-fail.html"
        try {
            paymentService.capture(request)
            return "pay-success.html"
        } catch (e: Exception) {
            return "pay-fail.html"
        }
    }

    @GetMapping("/pay/fail")
    suspend fun payFailed(request: ReqPayFailed): String {
        paymentService.authFailed(request)
        return "pay-fail.html"
    }
}
data class ReqPayFailed(
    val code: String,
    val message: String,
    val orderId: String,
)

data class ReqPaySucceed(
    val paymentKey: String,
    val orderId: String,
    val amount: Long,
    val paymentType: TossPaymentType,
)

enum class TossPaymentType {
    NORMAL, BRANDPAY, KEYIN
}

