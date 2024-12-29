package com.example.payment.common

import com.example.payment.application.OrderService
import com.example.payment.application.ProductService
import com.example.payment.infrastructure.ProductInOrderRepository
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Beans: ApplicationContextAware{

    companion object {
        lateinit var ctx: ApplicationContext
            private set

        fun <T : Any> getBean(byClass: KClass<T>, vararg arg: Any): T {
            return ctx.getBean(byClass.java, arg)
        }

        val beanProductInOrderRepository: ProductInOrderRepository by lazy{ getBean(ProductInOrderRepository::class) }
        val beanProductService: ProductService by lazy{ getBean(ProductService::class) }
        val beanOrderService: OrderService by lazy{ getBean(OrderService::class) }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }
}