package com.backstage.curtaincall.chat.service;

import com.backstage.curtaincall.chat.document.Chat;
import com.backstage.curtaincall.chat.document.ChatMessage;
import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, ChatMessageDto> redisTemplate;


    public void saveMessage(ChatMessageDto messageDto) {

        ChatMessage chatMessage = ChatMessage.create(messageDto.getRoomId(), messageDto.getSender(), messageDto.getContent());

        Optional<Chat> chatOptional = chatRepository.findById(messageDto.getRoomId());
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            chat.getMessages().add(chatMessage);
            chatRepository.save(chat);
            return;
        }

        Chat chat = Chat.create(messageDto.getRoomId());
        chat.getMessages().add(chatMessage);
        chatRepository.save(chat);
    }

    public void sendMessage(ChatMessageDto message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    public Page<ChatMessageDto> findMessagesByRoomId(String roomId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        long totalCount = chatRepository.getTotalCountById(roomId);

        List<ChatMessageDto> content = Optional.ofNullable(
                        chatRepository.findAllByIdWithPaging(roomId, pageRequest.getOffset(), pageRequest.getPageSize()))
                .map(chat -> chat.getMessages().stream()
                        .map(m -> new ChatMessageDto(m.getRoomId(), m.getSender(), m.getContent()))
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);

        return new PageImpl<>(content, pageRequest, totalCount);
    }


}
