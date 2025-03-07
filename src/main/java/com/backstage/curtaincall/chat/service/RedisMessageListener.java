package com.backstage.curtaincall.chat.service;

import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, ChatMessageDto> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            ChatMessageDto chatMessageDto = (ChatMessageDto) redisTemplate.getValueSerializer()
                    .deserialize(message.getBody());
            log.info("redis 메시지 호출", chatMessageDto);
            if (chatMessageDto != null) {
                chatService.saveMessage(chatMessageDto);
                messagingTemplate.convertAndSend("/queue/chat/" + chatMessageDto.getRoomId(), chatMessageDto);
            }
        } catch (Exception e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }
}
