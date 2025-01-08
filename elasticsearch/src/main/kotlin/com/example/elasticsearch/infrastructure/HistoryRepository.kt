package com.example.elasticsearch.infrastructure

import com.example.elasticsearch.domain.History
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository: CoroutineCrudRepository<History, Long>