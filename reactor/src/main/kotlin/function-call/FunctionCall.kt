package `function-call`

import mu.KotlinLogging
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

fun getRequest(): Mono<Int> {
    return Mono.just(1)
}

fun subA(request: Int): Mono<Int> {
    return Mono.fromCallable { request + 1 }
}

fun subB(param: Int): Mono<Int> {
    return Mono.fromCallable { param + 2 }
}

fun main() {

//    val request = getRequest().doOnNext { logger.debug { ">> request: ${it}" } }
//
//    val resA = subA(request).doOnNext{ logger.debug { ">> resA: ${it}" } }
//
//    val resB = subB(resA).doOnNext{ logger.debug { ">> resB: ${it}" } }
//
//    resB.subscribe()

    getRequest()
        .doOnNext { logger.info { ">> request: ${it}" } }
        .flatMap { subA(it) }
        .doOnNext { logger.info { ">> subA: ${it}" } }
        .flatMap { subB(it) }
        .doOnNext { logger.info { ">> subB: ${it}" } }
        .subscribe()

}