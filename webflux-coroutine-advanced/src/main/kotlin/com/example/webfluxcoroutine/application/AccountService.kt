package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.domain.Article
import com.example.webfluxcoroutine.exception.NotFoundException
import kotlinx.coroutines.delay
import com.example.webfluxcoroutine.repository.ArticleRepository as AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val repository: AccountRepository
){
    suspend fun get(id: Long): ResAccount {
        return repository.findById(id)?.toResAccount() ?: throw NotFoundException("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long) {
        repository.findById(id)?.let { account ->
            delay(3000)
            account.balance += amount
            repository.save(account)
        } ?: throw NotFoundException("id: $id")
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