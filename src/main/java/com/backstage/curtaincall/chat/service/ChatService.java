package com.backstage.curtaincall.chat.service;

import com.backstage.curtaincall.chat.document.Chat;
import com.backstage.curtaincall.chat.document.ChatMessage;
import com.backstage.curtaincall.chat.dto.ChatMessageDto;
import com.backstage.curtaincall.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

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
}
