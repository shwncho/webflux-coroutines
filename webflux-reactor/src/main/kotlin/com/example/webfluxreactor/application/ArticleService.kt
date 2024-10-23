package com.example.webfluxreactor.application

import com.example.webfluxreactor.domain.Article
import com.example.webfluxreactor.exception.NoArticleException
import com.example.webfluxreactor.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    @Transactional
    fun create(request: ReqCreate): Mono<Article> {
        return articleRepository.save(request.toArticle())
    }

    fun get(id: Long): Mono<Article> {
        return articleRepository.findById(id)
            .switchIfEmpty { throw NoArticleException("No article(id: $id) found") }
    }

    fun getAll(title: String? = null): Flux<Article> {
        if(title.isNullOrEmpty())   return articleRepository.findAll()
        return articleRepository.findAllByTitleContains(title)
    }

    @Transactional
    fun update(id: Long, request: ReqUpdate): Mono<Article> {
        return articleRepository.findById(id)
            .switchIfEmpty { throw NoArticleException("No article(id: $id) found") }
            .flatMap { article ->
                request.title?.let { article.title = it }
                request.body?.let { article.body = it }
                request.authorId?.let { article.authorId = it }
                articleRepository.save(article)
            }
    }

    @Transactional
    fun delete(id: Long): Mono<Void> {
        return articleRepository.deleteById(id)
    }

}

data class ReqCreate(
    val title: String,
    var body: String? = null,
    var authorId: Long? = null,
){

    fun toArticle(): Article{
        return Article(
            title = this.title,
            body = this.body,
            authorId = this.authorId,
        )
    }
}

data class ReqUpdate(
    val title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)