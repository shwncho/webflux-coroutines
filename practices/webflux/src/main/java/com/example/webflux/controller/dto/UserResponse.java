package com.example.webflux.controller.dto;

import com.example.webflux.common.Article;
import com.example.webflux.common.Image;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class UserResponse {
    private final String id;
    private final String name;
    private final int age;
    private final Long followCount;
}
