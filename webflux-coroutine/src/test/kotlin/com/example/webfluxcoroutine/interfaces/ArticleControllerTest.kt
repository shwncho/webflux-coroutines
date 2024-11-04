package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.ReqCreate
import com.example.webfluxcoroutine.repository.ArticleRepository
import org.junit.jupiter.api.Assertions.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val repository: ArticleRepository,
): StringSpec({

    val client = WebTestClient.bindToApplicationContext(context).build()

    fun getArticleSize(): Int {
        var size = 0
        client.get().uri("/article/all").accept(APPLICATION_JSON)
            .exchange()
            .expectBody()
            .jsonPath("$.length()").value<Int> { size = it }
        return size
    }

    "create" {
        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }
})