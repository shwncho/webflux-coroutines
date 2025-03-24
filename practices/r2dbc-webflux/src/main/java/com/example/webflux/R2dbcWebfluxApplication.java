package com.example.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@EnableR2dbcAuditing
@SpringBootApplication
public class R2dbcWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(R2dbcWebfluxApplication.class, args);
    }

}
