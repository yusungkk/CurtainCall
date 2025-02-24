package com.backstage.curtaincall.chat.repository;

import com.backstage.curtaincall.chat.document.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}
