package com.example.mvc.repository

import com.example.mvc.domain.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: JpaRepository<Article, Long> {

    fun findAllByTitleContains(title: String): List<Article>

    fun countByTitleContains(title: String): Long

}