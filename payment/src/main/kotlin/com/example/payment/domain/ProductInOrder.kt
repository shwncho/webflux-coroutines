package com.example.payment.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_PROD_IN_ORDER")
class ProductInOrder(
    val orderId: Long,
    var prodId: Long,
    var price: Long,
    var quantity: Int,
    @Id
    val seq: Long = 0,
): BaseEntity() {
    override fun equals(other: Any?): Boolean = kotlinEquals(other, arrayOf(
        ProductInOrder::orderId,
        ProductInOrder::prodId,
    ))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(
        ProductInOrder::orderId,
        ProductInOrder::prodId,
    ))
    override fun toString(): String = kotlinToString(arrayOf(
        ProductInOrder::orderId,
        ProductInOrder::prodId,
        ProductInOrder::price,
        ProductInOrder::quantity,
    ), superToString = { super.toString() })
}