package subscribe

import mu.KotlinLogging
import reactor.core.publisher.Flux
import java.time.Duration
import reactor.core.scheduler.Schedulers


private val logger = KotlinLogging.logger {}

//val workers = Schedulers.newSingle("single")
val workers = Schedulers.newParallel("sample", 2)

fun main() {
    Flux.range(1,12)
        .doOnNext { logger.debug { "1st: $it" } }
//        .publishOn(workers)
//        .delayElements(Duration.ofMillis(100), workers)
        .publishOn(workers)
        .delayElements(Duration.ofMillis(100)) // publishOn -> parallel
        .publishOn(workers) // 위 쪽 체인은 그대로 두고, 아래쪽 체인만 모두 worker 에서 처리
        .filter{ it % 2 == 0 }
        .doOnNext { logger.debug { "2st: $it" } }
        .filter{ it % 3 == 0 }
        .doOnNext { logger.debug { "3rd: $it" } }
        .filter{ it % 4 == 0 }
        .doOnNext { logger.debug { "4th: $it" } }
        .subscribeOn(workers)
        .subscribe()
}