package com.backstage.curtaincall.chat.controller;

import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageDto message) {
        log.info("[{}] {}: {}", roomId, message.getSender(), message.getContent());
        chatService.sendMessage(message);
    }


    @GetMapping("/api/v1/chat/{roomId}")
    public ResponseEntity<Page<ChatMessageDto>> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        return ResponseEntity.ok(chatService.findMessagesByRoomId(roomId, offset, limit));
    }
}
