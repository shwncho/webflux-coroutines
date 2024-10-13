package com.example.mvc.service

import com.example.mvc.repository.ArticleRepository
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("classpath:db-init/test.sql")
@ExtendWith(MockitoExtension::class)
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val repository: ArticleRepository,
) {

    @Test
    fun getAll() {
        assertEquals(3, repository.count())
        assertEquals(1, articleService.getAll("2").size)
        assertEquals(1, repository.countByTitleContains("2"))
    }

    @Test
    fun get() {
        articleService.get(1).let {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }
        assertThrows<Throwable> {
            articleService.get(-1)
        }
    }

    @Test
    fun create() {
        val request = ReqCreate("title 4", "blabla 04", 1234)
        articleService.create(request).let {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }
    }

    @Test
    fun update() {
        val newAuthorId = 999_999L
        articleService.update(1, ReqUpdate(authorId = newAuthorId)).let {
            assertEquals(newAuthorId, it.authorId)
        }
    }

    @Test
    fun delete() {
        val prevSize = repository.count()
        val new = articleService.create(ReqCreate("title 4", "blabla 04", 1234))
        assertEquals(prevSize.toInt() + 1, articleService.getAll().size)
        articleService.delete(new.id)
        assertEquals(prevSize, repository.count())
    }

}