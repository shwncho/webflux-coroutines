package com.example.payment.interfaces

import com.example.payment.application.OrderService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewController(
    private val orderService: OrderService,
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
        if(!orderService.authSucceed(request))
            return "pay-fail.html"
        orderService.capture(request)
        return "pay-success.html"
    }

    @GetMapping("/pay/fail")
    suspend fun payFailed(request: ReqPayFailed): String {
        orderService.authFailed(request)
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

