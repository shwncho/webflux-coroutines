package com.example.webfluxcoroutine.repository

import com.example.webfluxcoroutine.domain.Article
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long> {
    suspend fun findAllByTitleContains(title: String): Flow<Article>
}