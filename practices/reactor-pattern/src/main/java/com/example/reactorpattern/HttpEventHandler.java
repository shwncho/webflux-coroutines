package com.example.reactorpattern;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HttpEventHandler implements EventHandler{
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final SocketChannel clientSocket;
    private final MsgCodec msgCodec;

    @SneakyThrows
    public HttpEventHandler(Selector selector, SocketChannel clientSocket) {
        this.clientSocket = clientSocket;
        this.clientSocket.configureBlocking(false);
        this.clientSocket.register(selector, SelectionKey.OP_READ).attach(this);
        this.msgCodec = new MsgCodec();
    }

    @Override
    public void handle() {
        String requestBody = handleRequest(this.clientSocket);
        log.info("requestBody: {}", requestBody);
        sendResponse(this.clientSocket, requestBody);
    }

    /**
     * GET /?name=test HTTP/1.1
     * Host: localhost:8080
     * Connection: Keep-Alive
     * User-Agent: Apache-HttpClient/4.5.14 (Java/17.0.6)
     * Accept-Encoding: br,deflate,gzip,x-gzip
     *
     */
    @SneakyThrows
    private String handleRequest(SocketChannel clientSocket) {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        clientSocket.read(requestByteBuffer);
        return msgCodec.decode(requestByteBuffer);
    }

    @SneakyThrows
    private void sendResponse(SocketChannel clientSocket, String requestBody) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10);

                ByteBuffer responseByteBuffer = msgCodec.encode(requestBody);
                clientSocket.write(responseByteBuffer);
                clientSocket.close();
            } catch (Exception e) { }
        }, executorService);
    }
}
