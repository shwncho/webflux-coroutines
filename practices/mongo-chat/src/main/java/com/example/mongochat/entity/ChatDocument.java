package com.example.mongochat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@AllArgsConstructor
public class ChatDocument {
    @Id private final ObjectId id;
    private final String from;
    private final String to;
    private final String message;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public ChatDocument(String from, String to, String message) {
        this(null,from,to,message,null,null);
    }
    public ChatDocument withId(ObjectId id)
    {
        return new ChatDocument(id, this.from, this.to, this.message, this.createdAt, this.updatedAt);
    }

}
