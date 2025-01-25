package com.example.nioserver;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class JavaMultiIOClient {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start main");

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        long start = System.currentTimeMillis();
        for(int i = 0; i<1000; i++){
            var future = CompletableFuture.runAsync( () -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("127.0.0.1", 8080));

                    OutputStream out = socket.getOutputStream();
                    String requestBody = "This is Client";
                    out.write(requestBody.getBytes());
                    out.flush();

                    InputStream in = socket.getInputStream();
                    byte[] responseBytes = new byte[1024];
                    in.read(responseBytes);
                    log.info("result: {}", new String(responseBytes).trim());
                } catch (Exception e) {}
            }, executorService);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        log.info("end main");
        long end = System.currentTimeMillis();
        log.info("cost time: " + (end - start) / 1000.0 + "s");
    }
}
