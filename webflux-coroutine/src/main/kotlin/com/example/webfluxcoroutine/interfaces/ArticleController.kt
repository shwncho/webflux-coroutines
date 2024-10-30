package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.ArticleService
import com.example.webfluxcoroutine.application.ReqCreate
import com.example.webfluxcoroutine.domain.Article
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ReqCreate): Article {
        return articleService.create(request)
    }
}