package com.example.elasticsearch.interfaces

import com.example.elasticsearch.domain.History
import com.example.elasticsearch.domain.PgStatus
import com.example.elasticsearch.infrastructure.HistoryNativeRepository
import com.example.elasticsearch.infrastructure.HistoryRepository
import com.example.elasticsearch.infrastructure.QrySearch
import com.example.elasticsearch.infrastructure.ResSearch
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/history")
class HistoryController (
    private val repository: HistoryRepository,
    private val nativeRepository: HistoryNativeRepository,
) {

    @GetMapping("/{orderId}")
    suspend fun get(@PathVariable orderId: Long): History? {
        return repository.findById(orderId)
    }

    @GetMapping("/all")
    suspend fun getAll(): Flow<History> {
        return repository.findAll()
    }

    @PostMapping
    suspend fun save(@RequestBody request: ReqSaveHistory): History {
        return repository.findById(request.orderId)?.let { history ->
            request.userId?.let { history.userId = it }
            request.description?.let { history.description = it }
            request.amount?.let { history.amount = it }
            request.pgStatus?.let { history.pgStatus = it }
            request.createdAt?.let { history.createdAt = it }
            request.updatedAt?.let { history.updatedAt = it }
            repository.save(history)
        } ?: repository.save(request.toHistory())
    }

    @DeleteMapping("/{orderId}")
    suspend fun delete(@PathVariable orderId: Long) {
        repository.deleteById(orderId)
    }

    @DeleteMapping("/all")
    suspend fun deleteAll() {
        repository.deleteAll()
    }

    @GetMapping("/search")
    suspend fun search(request: QrySearch): ResSearch {
        return nativeRepository.search(request)
    }

}


data class ReqSaveHistory(
    var orderId: Long,
    var userId: Long?,
    var description: String?,
    var amount: Long?,
    var pgStatus: PgStatus?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    fun toHistory(): History {
        return this.let { History(
            orderId     = it.orderId,
            userId      = it.userId ?: 0,
            description = it.description ?: "",
            amount      = it.amount ?: 0,
            pgStatus      = it.pgStatus ?: PgStatus.CREATE,
            createdAt   = it.createdAt ?: LocalDateTime.now(),
            updatedAt   = it.updatedAt ?: LocalDateTime.now(),
        )}
    }
}