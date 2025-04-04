package com.example.mongochat.handler;

import com.example.mongochat.entity.ChatDocument;
import com.example.mongochat.repository.ChatMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMongoRepository chatMongoRepository;

    private static Map<String, Sinks.Many<Chat>> chatSinkMap =
            new ConcurrentHashMap<>();

    public Flux<Chat> register(String iam) {
        Sinks.Many<Chat> sink = Sinks.many().unicast().onBackpressureBuffer();

        chatSinkMap.put(iam, sink);
        return sink.asFlux();
    }

    public void sendChat(String from, String to, String message) {
        log.info("from: {}, to: {}, message: {}", from, to, message);
        var documentToSave = new ChatDocument(from, to, message);
        chatMongoRepository.save(documentToSave)
                .subscribe();
    }

    private void doSend(String from, String to, String message) {
        Sinks.Many<Chat> sink = chatSinkMap.get(to);

        if (sink == null) {
            Sinks.Many<Chat> my = chatSinkMap.get(from);
            my.tryEmitNext(new Chat("대화 상대가 없습니다", "system"));
            return;
        }

        sink.tryEmitNext(new Chat(message, from));
    }
}
