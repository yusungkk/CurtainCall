package com.backstage.curtaincall.chat.controller;

import com.backstage.curtaincall.chat.dto.ChatRoom;
import com.backstage.curtaincall.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestParam String user) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(user);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    @PostMapping("/assign")
    public ResponseEntity<ChatRoom> assignAgent(@RequestParam String roomId, @RequestParam String agent) {
        ChatRoom chatRoom = chatRoomService.assignAgent(roomId, agent);
        return ResponseEntity.ok(chatRoom);
    }
}
