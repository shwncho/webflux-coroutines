package com.example.springwebflux.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class SimpleUrlHandlerMappingConfig {
    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        Map<String, Object> urlMap = Map.of(
                "/echo", new EchoWebSocketHandler(),
                "ws", new GreetWebSocketHandler(),
                "/greet", new GreetWebHandler()
        );
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1);
        mapping.setUrlMap(urlMap);

        return mapping;
    }
}
