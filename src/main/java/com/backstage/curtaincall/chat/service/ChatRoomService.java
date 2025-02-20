package com.backstage.curtaincall.chat.service;

import com.backstage.curtaincall.chat.dto.ChatRoom;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomService {

    Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public ChatRoom createChatRoom(String user) {
        String roomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = new ChatRoom(roomId, user, null);
        chatRooms.put(roomId, chatRoom);
        return chatRoom;
    }

    public List<ChatRoom> getAllRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoom assignAgent(String roomId, String agent) {
        ChatRoom chatRoom = chatRooms.get(roomId);
        if (chatRoom != null && chatRoom.getAgent() == null) {
            chatRoom.setAgent(agent);
        }
        return chatRoom;
    }
}
