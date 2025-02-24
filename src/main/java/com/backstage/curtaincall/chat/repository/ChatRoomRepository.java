package com.backstage.curtaincall.chat.repository;

import com.backstage.curtaincall.chat.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    @Query("{'roomActive': ?0}")
    List<ChatRoom> findAllByRoomActive(String roomActive);
}
