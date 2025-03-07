package com.backstage.curtaincall.chat.controller;


import com.backstage.curtaincall.chat.document.RoomActive;
import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.chat.dto.ChatRoomDto;
import com.backstage.curtaincall.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate template;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@AuthenticationPrincipal UserDetails userDetails) {
        ChatRoomDto chatRoom = chatRoomService.createChatRoom(userDetails.getUsername());
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getAllRooms(@RequestParam String active) {
        List<ChatRoomDto> rooms = chatRoomService.getRooms(active);
        return ResponseEntity.ok(rooms);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoomActive(@RequestParam String roomId, @RequestParam String active) {

        if (active.equals(RoomActive.WITH_ADMIN.name())) {
            chatRoomService.assignAdmin(roomId, "admin");
            template.convertAndSend("/queue/chat/" + roomId, ChatMessageDto.enterAdminMessage(roomId));
        }

        if (active.equals(RoomActive.END.name())) {
            chatRoomService.endChatRoom(roomId);
            template.convertAndSend("/queue/chat/" + roomId, ChatMessageDto.endRoomMessage(roomId));
        }
    }
}
