package com.example.payment

import com.example.payment.domain.Order
import com.example.payment.domain.Product
import com.example.payment.domain.ProductInOrder
import com.example.payment.infrastructure.OrderRepository
import com.example.payment.infrastructure.ProductInOrderRepository
import com.example.payment.infrastructure.ProductRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class PaymentApplicationTests(
	@Autowired prodRepository: ProductRepository,
	@Autowired orderRepository: OrderRepository,
	@Autowired prodInOrderRepository: ProductInOrderRepository,
): StringSpec({
	"product" {
		val prevCnt = prodRepository.count()
		prodRepository.save(Product(1,"a",1000).apply { new = true })
		val currCnt = prodRepository.count()
		currCnt shouldBe prevCnt + 1
	}
	"order" {
		val prevCnt = orderRepository.count()
		orderRepository.save(Order(userId = 1)).also { logger.debug { it } }
		val currCnt = orderRepository.count()
		currCnt shouldBe prevCnt + 1
	}
	"prod in order" {
		val prevCnt = prodInOrderRepository.count()
		prodInOrderRepository.save(ProductInOrder(1,1,1,1)).also { logger.debug { it } }
		val currCnt = prodInOrderRepository.count()
		currCnt shouldBe prevCnt + 1
	}
})
