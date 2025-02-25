package com.backstage.curtaincall.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String roomId;
    private String sender;
    private String content;

    public static ChatMessageDto enterAdminMessage(String roomId) {
        return new ChatMessageDto(roomId, "system", "상담사가 입장하였습니다.");
    }

    public static ChatMessageDto endRoomMessage(String roomId) {
        return new ChatMessageDto(roomId, "system", "채팅상담이 종료되었습니다.");
    }
}
