package com.example.webfluxcoroutine.example

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    private val template: ReactiveRedisTemplate<Any,Any>
): StringSpec({

    val KEY = "key"

    afterTest {
        template.delete(KEY).awaitSingle()
    }

    "hello reactiv redis" {
        val ops = template.opsForValue()
        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }
        ops.set(KEY, "test").awaitSingle()
        ops.get(KEY).awaitSingle() shouldBe "test"

        template.expire(KEY, 3.seconds.toJavaDuration()).awaitSingle()
        delay(5.seconds)
        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }
    }

    "LinkedList" {
        val ops = template.opsForList()
        ops.rightPushAll(KEY,2,3,4,5).awaitSingle()

        template.type(KEY).awaitSingle() shouldBe DataType.LIST
        ops.size(KEY).awaitSingle() shouldBe 4

//        for(i in 0 until ops.size(KEY).awaitSingle()) {
//            ops.index(KEY,i).awaitSingle().let {
//                logger.debug { "$i: $it" }
//            }
//        }
//
//        ops.range(KEY,0,-1).asFlow().collect{ logger.debug { it } }
//        ops.range(KEY,0,-1).toStream().forEach(){ logger.debug { it } }

//        ops.range(KEY,0,-1).asFlow().toList() shouldBe listOf(2,3,4,5)
//        ops.all(KEY) shouldBe listOf(2,3,4,5)

        ops.rightPush(KEY,6).awaitSingle()
        ops.all(KEY) shouldBe listOf(2,3,4,5,6)

        ops.leftPop(KEY).awaitSingle() shouldBe 2
        ops.all(KEY) shouldBe listOf(3,4,5,6)

        ops.leftPush(KEY, 9).awaitSingle()
        ops.all(KEY) shouldBe listOf(9,3,4,5,6)
        ops.rightPop(KEY).awaitSingle() shouldBe 6
        ops.all(KEY) shouldBe listOf(9,3,4,5)
    }
})

suspend fun ReactiveListOperations<Any, Any>.all(key: Any): List<Any> {
    return this.range(key,0,-1).asFlow().toList()
}