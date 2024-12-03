package com.example.webfluxcoroutine.application

import com.example.webfluxcoroutine.config.extension.toLocalDate
import com.example.webfluxcoroutine.config.validator.DateString
import com.example.webfluxcoroutine.domain.Article
import com.example.webfluxcoroutine.exception.NotFoundException
import com.example.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val dbClient: DatabaseClient,
    redisTemplate: ReactiveRedisTemplate<Any, Any>
) {

    private val ops = redisTemplate.opsForValue()

    @Transactional
    suspend fun create(request: ReqCreate): Article{
        return articleRepository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article{
        val key = SimpleKey("/article/get",id)
        return ops.get(key).awaitSingleOrNull()?.let { it as Article }
            ?: articleRepository.findById(id)?.also { ops.set(key,it,10.seconds.toJavaDuration()).awaitSingle() }
            ?: throw NotFoundException("id: $id")
    }

//    suspend fun getAll(title: String?): Flow<Article> {
//        if(title.isNullOrEmpty())   return articleRepository.findAll()
//        return articleRepository.findAllByTitleContains(title)
//    }

    suspend fun getAll(request: QryArticle): Flow<Article> {
        val params = HashMap<String,Any>()
        var sql = dbClient.sql("""
            SELECT id, title, body, author_id, created_at, updated_at
            FROM   TB_ARTICLE
            WHERE  1=1
            ${ request.title.query {
            params["title"] = it.trim().let { "%$it%" }
            "AND title LIKE :title"
        }}
            ${ request.authorId.query {
            params["authorId"] = it
            "AND author_id IN (:authorId)"
        }}
            ${ request.from.query {
            params["from"] = it.toLocalDate()
            "AND created_at >= :from"
        }}
            ${ request.to.query {
            params["to"] = it.toLocalDate().plusDays(1)
            // 2023-01-20 -> 2023-01-21 00:00:00.000
            // <= -> <
            "AND created_at < :to"
        }}
        """.trimIndent())
        params.forEach { key, value -> sql = sql.bind(key,value) }
        return sql.map { row ->
            Article(
                id       = row.get("id") as Long,
                title    = row.get("title") as String,
                body     = row.get("body") as String?,
                authorId = row.get("author_id") as Long,
            ).apply {
                createdAt = row.get("created_at") as LocalDateTime?
                updatedAt = row.get("updated_at") as LocalDateTime?
            }
        }.flow()
    }

    @Transactional
    suspend fun update(id: Long, request: ReqUpdate): Article {
        val article = articleRepository.findById(id) ?: throw NotFoundException("id: $id")
        return articleRepository.save(article.apply {
            request.title?.let { title = it }
            request.body?.let { body = it }
            request.authorId?.let { authorId = it }
        }).also {
            val key = SimpleKey("/article/get", id)
            ops.delete(key).awaitSingle()
        }
    }

    @Transactional
    suspend fun delete(id: Long) {
        articleRepository.deleteById(id).also {
            val key = SimpleKey("/article/get", id)
            ops.delete(key).awaitSingle()
        }
    }
}

fun <T> T?.query(f: (T) -> String): String{
    return when{
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
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

data class ReqUpdate(
    var title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)

data class QryArticle(
    val title: String?,
    val authorId: List<Long>?,
    @DateString
    val from: String?,
    @DateString
    val to: String?,
)