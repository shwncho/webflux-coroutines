package com.example.payment.application

import com.example.payment.config.extension.toLocalDate
import com.example.payment.domain.Order
import com.example.payment.domain.PgStatus
import com.example.payment.domain.PgStatus.*
import com.example.payment.infrastructure.OrderRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class OrderHistoryServiceTest(
    @Autowired private val orderHistoryService: OrderHistoryService,
    @Autowired private val orderRepository: OrderRepository,
): StringSpec({

    afterTest {
        orderRepository.deleteAll()
    }

    "filter valid status" {
        listOf(
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CREATE),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_SUCCESS),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_FAIL),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_INVALID),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_REQUEST), //
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_RETRY), //
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_SUCCESS), //
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_FAIL),
        ).forEach { orderRepository.save(it) }

        orderHistoryService.getHistories(QryOrderHistory(userId = 11)).size shouldBe 3

    }

    "get order history" {

        var createdAt = "2024-01-01".toLocalDate().atStartOfDay()
        listOf(
            Order(userId = 11, description = "A,B", amount=1000),
            Order(userId = 11, description = "C", amount=1100),
            Order(userId = 11, description = "D,E,F", amount=1200),
            Order(userId = 11, description = "D,G,H", amount=1300),
            Order(userId = 11, description = "I,J", amount=1400),
            Order(userId = 11, description = "I,K", amount=1500),
            Order(userId = 11, description = "I,L,M", amount=1600),
            Order(userId = 11, description = "I,L,M,N", amount=1700),
            Order(userId = 11, description = "O", amount=1800),
            Order(userId = 11, description = "P,O", amount=1900),
            Order(userId = 11, description = "P,R", amount=2000),
        ).forEach {
            it.pgStatus = CAPTURE_SUCCESS
            orderRepository.save(it)
            it.createdAt = createdAt
            createdAt = createdAt.plusDays(1)
            orderRepository.save(it)
        }

        orderHistoryService.getHistories(QryOrderHistory(userId = 11, limit = 20)).size shouldBe 11

        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "A")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "B")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "C")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "A")).first().id shouldBe
                orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "B")).first().id

        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "D")).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "D, H")).size shouldBe 1

        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I")).size shouldBe 4
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I K")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I L")).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I L N")).size shouldBe 1

        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=2)).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=2, page=2)).first().id shouldBe
                orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=3, page=1)).last().id

        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=5, page=3)).size shouldBe 1

        orderHistoryService.getHistories(QryOrderHistory(userId=11, keyword="I", fromAmount=1450)).size shouldBe 3
        orderHistoryService.getHistories(QryOrderHistory(userId=11, keyword="I", fromAmount=1450, toAmount=1650)).size shouldBe 2

        orderHistoryService.getHistories(QryOrderHistory(userId=11, fromDate="2024-01-03")).size shouldBe 9
        orderHistoryService.getHistories(QryOrderHistory(userId=11, fromDate="2024-01-03", toDate="2024-01-08")).size shouldBe 6
    }
})