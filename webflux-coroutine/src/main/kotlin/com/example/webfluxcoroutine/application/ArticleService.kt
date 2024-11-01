package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.domain.Article
import com.example.webfluxcoroutine.exception.NotFoundException
import com.example.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    @Transactional
    suspend fun create(request: ReqCreate): Article{
        return articleRepository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article{
        return articleRepository.findById(id) ?: throw NotFoundException("id: $id")
    }

    suspend fun getAll(title: String?): Flow<Article> {
        if(title.isNullOrEmpty())   return articleRepository.findAll()
        return articleRepository.findAllByTitleContains(title)
    }
}











data class ReqCreate(
    var title: String,
    var body: String? = null,
    var authorId: Long? = null,
){
    fun toArticle(): Article {
        return Article(
            title = this.title,
            body = this.body,
            authorId = this.authorId
        )
    }
}