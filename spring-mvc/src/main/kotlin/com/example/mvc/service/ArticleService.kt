package com.example.mvc.service

import com.example.mvc.domain.Article
import com.example.mvc.exception.NoArticleFound
import com.example.mvc.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime

@Service
class ArticleService(
    private val repository: ArticleRepository,
) {

    fun getAll(): List<Article> {
        return repository.findAll()
    }

    fun getAll(title: String): List<Article> {
        return repository.findAllByTitleContains(title)
    }

    fun get(articleId: Long): Article {
        return repository.findByIdOrNull(articleId)
            ?: throw NoArticleFound("article id : $articleId")
    }

    @Transactional
    fun create(request: ReqCreate): Article {
        return repository.save(Article().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        })
    }

    @Transactional
    fun update(articleId: Long, request: ReqUpdate): Article {
        return repository.findByIdOrNull(articleId)?.let{ article ->
            request.title?.let { article.title = it }
            request.body?.let { article.body = it }
            request.authorId?.let { article.authorId = it }
            repository.save(article)
        } ?: throw NoArticleFound("article id : $articleId")
    }

    @Transactional
    fun delete(articleId: Long) {
        repository.deleteById(articleId)
    }

}

data class ReqCreate(
    val title: String,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ReqUpdate(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)