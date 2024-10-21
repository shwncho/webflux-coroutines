package com.example.webfluxreactor.repository

import com.example.webfluxreactor.domain.Article
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ArticleRepository: R2dbcRepository<Article, Long> {

    fun findAllByTitleContains(title: String): Flux<Article>

}