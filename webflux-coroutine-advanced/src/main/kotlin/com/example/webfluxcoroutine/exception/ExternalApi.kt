package com.example.webfluxcoroutine.exception

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ExternalApi(
    @Value("\${api.external.url}")
    private val externalUrl: String
){

    private val client = WebClient.builder().baseUrl(externalUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun delay(){
        return client.get().uri("/delay").retrieve().awaitBody()
    }
}