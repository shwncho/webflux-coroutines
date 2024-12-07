package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.config.Locker
import com.example.webfluxcoroutine.domain.Article
import com.example.webfluxcoroutine.exception.NotFoundException
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import com.example.webfluxcoroutine.repository.ArticleRepository as AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

@Service
class AccountService(
    private val repository: AccountRepository,
    private val locker: Locker
){
    private val logger = KotlinLogging.logger {}

    suspend fun get(id: Long): ResAccount {
        return repository.findById(id)?.toResAccount() ?: throw NotFoundException("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long) {
//        repository.findById(id)?.let { account ->
        logger.debug { "1. request" }
        repository.findArticleById(id)?.let { account ->
            logger.debug { "2. read data" }
            delay(3000)
            account.balance += amount
            repository.save(account)
            logger.debug { "3. update data" }
        } ?: throw NotFoundException("id: $id")
    }

    @Transactional
    suspend fun addBalance(id: Long, amount: Long): Article {
        val key = SimpleKey(AccountService::addBalance.name, id)
        return locker.lock(key){
            val account = repository.findById(id) ?: throw NotFoundException("id: ${id}")
            delay(3.seconds)
            account.balance += amount
            logger.debug { "before commit: ${account.toResAccount()}"}
            repository.save(account)
            repository.findById(id)!!
        }
    }
}


data class ResAccount(
    val id: Long,
    val balance: Long,
)

fun Article.toResAccount(): ResAccount {
    return ResAccount(
        id = this.id,
        balance = this.balance
    )
}