package `mono-flux`

import mu.KotlinLogging
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

fun main() {
    //단건
//    Mono.just(9).map { it + 1 }.doOnNext{
//        logger.debug { ">> from publisher -> ${it}"}
//    }.subscribe()
    //다건
//    Flux.just(1,3,5,7,9).map { it + 1 }.log().subscribe()

//    //map은 block 방식으로 처리
//    Flux.range(1,10).map { it * it}.log().subscribe()
//
//    //flatMap은 NIO로 처리
//    Flux.range(1,10).flatMap { Mono.just(it*it) }.log().subscribe()

    // Mono -> Flux 변환
    Mono.just(1).flux().log().subscribe()
}