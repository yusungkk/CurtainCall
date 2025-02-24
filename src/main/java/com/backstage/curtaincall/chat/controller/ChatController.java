package com.backstage.curtaincall.chat.controller;

import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageDto message) {
        log.info("[{}] {}: {}", roomId, message.getSender(), message.getContent());
        chatService.saveMessage(message);
        template.convertAndSend("/queue/chat/" + roomId, message);
    }
}
