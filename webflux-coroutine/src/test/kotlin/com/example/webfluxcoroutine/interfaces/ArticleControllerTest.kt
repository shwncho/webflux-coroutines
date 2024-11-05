package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.ReqCreate
import com.example.webfluxcoroutine.application.ReqUpdate
import com.example.webfluxcoroutine.domain.Article
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

    "create" {
        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "get all"{
        client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
        client.get().uri("/article/all?title=a").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
        client.get().uri("/article/all?title=te").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    "get"{
        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        val id = client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody(Article::class.java).returnResult().responseBody!!.id
        val res = client.get().uri("/article/$id").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody(Article::class.java).returnResult().responseBody!!

        res.title shouldBe request.title
        res.body shouldBe request.body
        res.authorId shouldBe request.authorId

        client.get().uri("/article/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    "update" {
        val request = ReqUpdate(authorId = 999999)
        client.put().uri("/article/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "delete" {
        val prevSize = repository.count()
        val res = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("test", "it is r2dbc demo", 1234)).exchange()
            .expectBody(Article::class.java).returnResult().responseBody!!

        repository.count() shouldBe prevSize + 1
        client.delete().uri("/article/${res.id}").exchange().expectStatus().isOk
        repository.count() shouldBe prevSize
    }
})