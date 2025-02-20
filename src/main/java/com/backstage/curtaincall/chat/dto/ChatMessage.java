package com.backstage.curtaincall.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String roomId;
    private String sender;
    private String content;
}
