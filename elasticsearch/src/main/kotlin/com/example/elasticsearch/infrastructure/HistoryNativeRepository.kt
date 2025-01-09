package com.example.elasticsearch.infrastructure

import com.example.elasticsearch.config.extension.toLocalDate
import com.example.elasticsearch.domain.History
import com.example.elasticsearch.domain.PgStatus
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.suggest.response.SortBy
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty

@Component
class HistoryNativeRepository(
    private val template: ReactiveElasticsearchTemplate,
) {

    suspend fun search(request: QrySearch): ResSearch {
        val criteria = Criteria().apply {
            request.orderId?.let { and(
                History::orderId.criteria.`in`(it)
            )}
            request.userId?.let { and(
                History::userId.criteria.`in`(it)
            )}
            request.keyword?.split(" ")?.toSet()?.forEach { and(
                History::description.criteria.contains(it)
            )}
            request.pgStatus?.let { and(
                History::pgStatus.criteria.`in`(it)
            )}
            request.fromDate?.toLocalDate()?.atStartOfDay()?.let { and(
                History::createdAt.criteria.greaterThanEqual(it)
            )}
            request.toDate?.toLocalDate()?.plusDays(1)?.atStartOfDay()?.let { and(
                History::createdAt.criteria.lessThan(it)
            )}
            request.fromAmount?.let { and(
                History::amount.criteria.greaterThanEqual(it)
            )}
            request.toAmount?.let { and(
                History::amount.criteria.lessThanEqual(it)
            )}
        }

        val query = CriteriaQuery(criteria, PageRequest.of(0, request.pageSize)).apply {
            sort = History::createdAt.sort(Sort.Direction.DESC)
            searchAfter = request.pageNext
        }

        return template.searchForPage(query, History::class.java).awaitSingle().let { res ->
            ResSearch(
                res.content.map { it.content },
                res.totalElements,
                res.content.lastOrNull()?.sortValues
            )
        }
    }
}

val KProperty<*>.criteria: Criteria
    get() = Criteria(this.name)

fun KProperty<*>.sort(direction: Sort.Direction = Sort.Direction.ASC): Sort {
    return Sort.by(direction, this.name)
}
data class QrySearch(
    val orderId: List<Long>?,
    val userId: List<Long>?,
    val keyword: String?,
    val pgStatus: List<PgStatus>,
    val fromDate: String,
    val toDate: String,
    val fromAmount: Long?,
    val toAmount: Long?,
    val pageSize: Int = 10,
    val pageNext: List<Long>? = null,
)

data class ResSearch(
    val items: List<History>,
    val total: Long,
    val pageNext: List<Any>?
)