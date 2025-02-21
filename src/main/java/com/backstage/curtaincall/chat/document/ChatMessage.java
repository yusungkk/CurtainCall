package com.backstage.curtaincall.chat.document;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessage {
    private String roomId;
    private String sender;
    private String content;
    private LocalDateTime createAt;

    private ChatMessage(String roomId, String sender, String content) {
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        createAt = LocalDateTime.now();
    }

    public static ChatMessage create(String roomId, String sender, String content) {
        return new ChatMessage(roomId, sender, content);
    }
}
