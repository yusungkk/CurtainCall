package com.backstage.curtaincall.chat.controller;


import com.backstage.curtaincall.chat.dto.ChatRoomDto;
import com.backstage.curtaincall.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestParam String user) {
        ChatRoomDto chatRoom = chatRoomService.createChatRoom(user);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getAllRooms(@RequestParam String active) {
        List<ChatRoomDto> rooms = chatRoomService.getRooms(active);
        return ResponseEntity.ok(rooms);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignAgent(@RequestParam String roomId) {
        chatRoomService.assignAdmin(roomId, "admin");
    }
}
