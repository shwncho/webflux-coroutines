package threadcost

import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

fun main() {
    val sum = AtomicLong()
    measureTimeMillis {
        Flux.range(1, 10_000).doOnNext {
            Flux.range(1, 100_000).doOnNext {
                sum.addAndGet(1)
            }.subscribe()
        }.blockLast()
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}