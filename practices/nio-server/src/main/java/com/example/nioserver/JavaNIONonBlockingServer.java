package com.example.nioserver;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JavaNIONonBlockingServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start main");
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            serverSocket.configureBlocking(false);

            while(true) {
                SocketChannel clientSocket = serverSocket.accept();
                if (clientSocket == null) {
                    Thread.sleep(100);
                    continue;
                }

                ByteBuffer requestByBuffer = ByteBuffer.allocateDirect(1024);
                while (clientSocket.read(requestByBuffer)==0) {
                    log.info("wait Reading");
                }
                requestByBuffer.flip();
                String requestBody = StandardCharsets.UTF_8.decode(requestByBuffer).toString();
                log.info("request: {}", requestBody);

                ByteBuffer responseByteBuffer = ByteBuffer.wrap("This is server".getBytes());
                clientSocket.write(responseByteBuffer);
                clientSocket.close();
            }
        }
    }
}
