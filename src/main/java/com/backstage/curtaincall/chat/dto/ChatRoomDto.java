package com.backstage.curtaincall.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomDto {
    private String roomId;
    private String user;
    private String admin;
}
