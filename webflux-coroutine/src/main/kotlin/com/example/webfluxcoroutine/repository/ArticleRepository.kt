package com.example.webfluxcoroutine.repository

import com.example.webfluxcoroutine.domain.Article
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long> {
}