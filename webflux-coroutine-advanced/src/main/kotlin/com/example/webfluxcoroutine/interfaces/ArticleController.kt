package com.example.webfluxcoroutine.interfaces

import com.example.webfluxcoroutine.application.ArticleService
import com.example.webfluxcoroutine.application.QryArticle
import com.example.webfluxcoroutine.application.ReqCreate
import com.example.webfluxcoroutine.application.ReqUpdate
import com.example.webfluxcoroutine.domain.Article
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        return articleService.get(id)
    }

//    @GetMapping("/all")
////    suspend fun getAll(@RequestParam title: String?): Flow<Article> {
////        return articleService.getAll(title)
////    }

    @GetMapping("/all")
    suspend fun getAll(request: QryArticle): Flow<Article> {
        return articleService.getAllCached(request)
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: Long, @RequestBody request: ReqUpdate): Article {
        return articleService.update(id, request)
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
        articleService.delete(id)
    }

}