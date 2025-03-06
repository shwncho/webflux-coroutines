package com.example.webflux.filter;

import com.example.webflux.auth.IamAuthentication;
import com.example.webflux.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RequiredArgsConstructor
@Component
public class SecurityWebFilter implements WebFilter {
    private final AuthService authService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final ServerHttpResponse response = exchange.getResponse();
        String iam = exchange.getRequest().getHeaders().getFirst("X-I-AM");

        if(iam == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return authService.getNameByToken(iam)
                .map(IamAuthentication::new)
                .flatMap(authentication -> {
                    return chain.filter(exchange)
                            .contextWrite(context -> {
                                Context newContext = ReactiveSecurityContextHolder
                                        .withAuthentication(authentication);

                                return context.putAll(newContext);
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }));
    }
}
