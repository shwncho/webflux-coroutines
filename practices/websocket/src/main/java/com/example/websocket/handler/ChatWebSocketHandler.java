package com.example.websocket.handler;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    @Data
    private static class Chat {
        private final String message;
        private final String from;
    }

    private static Map<String, Sinks.Many<Chat>> chatSinkMap =
            new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String iam = (String) session.getAttributes().get("iam");
        Sinks.Many<Chat> sink = Sinks.many().unicast().onBackpressureBuffer();

        chatSinkMap.put(iam, sink);
        sink.tryEmitNext(new Chat(iam + "님 채팅방에 오신 것을 환영합니다.", "system"));

        session.receive()
                .doOnNext(webSocketMessage -> {
                    String payload = webSocketMessage.getPayloadAsText();

                    String[] splits = payload.split(":");
                    String to = splits[0].trim();
                    String message = splits[1].trim();

                    Sinks.Many<Chat> targetSink = chatSinkMap.get(to);
                    if(targetSink != null) {
                        if(targetSink.currentSubscriberCount() > 0) {
                            targetSink.tryEmitNext(new Chat(message, iam));
                        }
                    } else {
                        sink.tryEmitNext(new Chat("존재하지 않는 대화 상대 입니다.", "system"));
                    }
                }).subscribe();

        return session.send(sink.asFlux()
                .map(chat -> session.textMessage(chat.from + ": " + chat.message))
        );
    }
}
