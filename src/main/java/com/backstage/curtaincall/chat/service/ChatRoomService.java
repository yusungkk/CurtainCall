package com.backstage.curtaincall.chat.service;

import com.backstage.curtaincall.chat.document.ChatRoom;
import com.backstage.curtaincall.chat.dto.ChatRoomDto;
import com.backstage.curtaincall.chat.repository.ChatRoomRepository;
import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.backstage.curtaincall.chat.document.RoomActive.WITHOUT_ADMIN;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomDto createChatRoom(String username) {
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        ChatRoom room = ChatRoom.create(roomId, username);
        chatRoomRepository.save(room);
        return new ChatRoomDto(roomId, username, room.getAdminName());
    }

    public List<ChatRoomDto> getRooms(String active) {
        return chatRoomRepository.findAllByRoomActive(active).stream()
                .map(cr -> new ChatRoomDto(cr.getId(), cr.getUsername(), cr.getAdminName()))
                .toList();
    }

    public void assignAdmin(String roomId, String adminName) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHAT_ROOM_NOT_FOUND));

        if (chatRoom.getRoomActive().equals(WITHOUT_ADMIN)) {
            chatRoom.enterAdmin(adminName);
            chatRoomRepository.save(chatRoom);
        }
    }

    public void endChatRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.endRoom();
        chatRoomRepository.save(chatRoom);
    }
}
