import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    var sum = AtomicLong()
    measureTimeMillis {
        runBlocking {
            repeat(10_000) {
                launch {
                    repeat(100_000) {
                        sum.addAndGet(1)
                    }
                }
            }
        }
    }.let { logger.debug(">> sum: $sum, done $it ms") }
}