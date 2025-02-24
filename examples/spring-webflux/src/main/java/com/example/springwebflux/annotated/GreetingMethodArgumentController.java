package com.example.springwebflux.annotated;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@RequestMapping("/method-arguments")
@RestController
public class GreetingMethodArgumentController {
    @GetMapping("/swe")
    Mono<String> serverWebExchange(
            ServerWebExchange serverWebExchange
    ) {
        String name = serverWebExchange.getRequest()
                .getQueryParams()
                .getFirst("name");

        if(name == null) name = "world";

        return Mono.just("Hello " + name);
    }

    @GetMapping("/reqres")
    Mono<String> serverReqRes(
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        String name = request
                .getQueryParams()
                .getFirst("name");

        if(name == null)    name = "world";

        response.setStatusCode(
                HttpStatus.CREATED
        );
        return Mono.just("Hello " + name);
    }

    @GetMapping("/session")
    Mono<String> session(
            WebSession session,
            ServerWebExchange serverWebExchange
    ) {
        String savedName = session.getAttribute("name");
        String name;
        if(savedName != null)    name = savedName;
        else {
            name = serverWebExchange.getRequest()
                    .getQueryParams()
                    .getFirst("name");

            if(name == null) name = "world";
            session.getAttributes().put("name", name);
        }

        return Mono.just("Hello " + name);
    }
}
