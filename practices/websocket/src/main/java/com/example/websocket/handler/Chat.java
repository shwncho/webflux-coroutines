package com.example.websocket.handler;

import lombok.Data;

@Data
public class Chat {
    private final String message;
    private final String from;
}
