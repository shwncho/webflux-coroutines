package com.example.mongochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@EnableReactiveMongoAuditing
@SpringBootApplication
public class MongoChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoChatApplication.class, args);
    }

}
