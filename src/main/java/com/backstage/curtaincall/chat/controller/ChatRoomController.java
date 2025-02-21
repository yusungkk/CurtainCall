package com.backstage.curtaincall.chat.controller;


import com.backstage.curtaincall.chat.dto.ChatRoomDto;
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
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestParam String user) {
        ChatRoomDto chatRoom = chatRoomService.createChatRoom(user);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    @PostMapping("/assign")
    public ResponseEntity<ChatRoomDto> assignAgent(@RequestParam String roomId, @RequestParam String admin) {
        ChatRoomDto chatRoom = chatRoomService.assignAdmin(roomId, admin);
        return ResponseEntity.ok(chatRoom);
    }
}
