package com.example.mongochat.handler;

import lombok.Data;

@Data
public class Chat {
    private final String message;
    private final String from;
}
