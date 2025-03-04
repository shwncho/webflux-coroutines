package com.example.springwebflux.webclient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class WebClientExample {
    @Data
    private static class UserInfo {
        private final String id;
        private final String name;
        private final String email;
    }

    public static void main(String[] args) {
        WebClient webClient = WebClient.create("https://localhost:8081");

        ResponseEntity<UserInfo> response = webClient
                .get()
                .uri("/user")
                .header("X-Request-Id", "1234")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(UserInfo.class)
                .block();

        assert response.getBody().getId().equals("1234");
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getHeaders().getContentType()
                .toString().equals("application/json");
        assert response.getHeaders().get("X-Request-Id")
                .get(0).equals("1234");
    }
}
