package com.backstage.curtaincall.chat.repository;

import com.backstage.curtaincall.chat.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
