package com.backstage.curtaincall.chat.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static com.backstage.curtaincall.chat.document.RoomActive.WITHOUT_ADMIN;
import static com.backstage.curtaincall.chat.document.RoomActive.WITH_ADMIN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Document(collection = "chat_room")
public class ChatRoom {

    @Id
    private String id;
    private String username;
    private String adminName;
    private RoomActive roomActive;
    private LocalDateTime createAt;

    private ChatRoom(String roomId, String username) {
        this.id = roomId;
        this.username = username;
        this.adminName = "";
        this.roomActive = WITHOUT_ADMIN;
        this.createAt = LocalDateTime.now();
    }

    public static ChatRoom create(String roomId, String username) {
        return new ChatRoom(roomId, username);
    }

    public void enterAdmin(String adminName) {
        this.adminName = adminName;
        this.roomActive = WITH_ADMIN;
    }

    public void endRoom() {
        this.roomActive = RoomActive.END;
    }
}
