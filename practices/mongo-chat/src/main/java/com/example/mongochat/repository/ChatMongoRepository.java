package com.example.mongochat.repository;

import com.example.mongochat.entity.ChatDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChatMongoRepository extends ReactiveMongoRepository<ChatDocument, ObjectId> {
}
