package com.example.springwebflux;

public class CustomException  extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
