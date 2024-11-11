package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.exception.NotFoundException
import com.example.webfluxcoroutine.repository.ArticleRepository
import org.junit.jupiter.api.Assertions.*

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator,
): StringSpec({

    "get all" {
        rxtx.rollback {
            repository.deleteAll()
            articleService.create(ReqCreate("title 1", "blabla 01", 1234))
            articleService.create(ReqCreate("title 2", "blabla 02", 1234))
            articleService.create(ReqCreate("title 3", "blabla 03", 1234))
            articleService.getAll(null).toList().size shouldBe 3
            articleService.getAll("2").toList().size shouldBe 1
        }
    }

    "get" {
        rxtx.rollback {
            val new = articleService.create(ReqCreate("title 1", "blabla 01", 1234))
            articleService.get(new.id).let {
                it.title shouldBe  "title 1"
                it.body shouldBe "blabla 01"
                it.authorId shouldBe 1234
            }
        }
        shouldThrow<NotFoundException> {
            articleService.get(-1)
        }
    }

    "create" {
        val request = ReqCreate("title 4", "blabla 04", 1234)
        rxtx.rollback {
            articleService.create(request).let {
                it.title shouldBe request.title
                it.body shouldBe request.body
                it.authorId shouldBe request.authorId
            }
        }
    }

    "update" {
        val new = articleService.create(ReqCreate("title 1", "blabla 01", 1234))
        val newAuthorId = 999_999L
        rxtx.rollback {
            val updated = articleService.update(new.id, ReqUpdate(authorId=newAuthorId))
            updated.authorId shouldBe newAuthorId
        }
    }

    "delete" {
        rxtx.rollback {
            val prevSize = repository.count()
            val created = articleService.create(ReqCreate("title 4", "blabla 04", 1234))
            repository.count() shouldBe prevSize + 1
            articleService.delete(created.id)
            repository.count() shouldBe prevSize
        }
    }

})

suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T): T {
    return executeAndAwait { tx ->
        tx.setRollbackOnly()
        f.invoke(tx)
    }
}