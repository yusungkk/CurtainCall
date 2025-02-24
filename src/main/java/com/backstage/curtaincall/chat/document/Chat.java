package com.backstage.curtaincall.chat.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Document(collection = "chat")
public class Chat {

    @Id
    private String id;
    private List<ChatMessage> messages = new ArrayList<>();

    private Chat(String id) {
        this.id = id;
    }

    public static Chat create(String roomId) {
        return new Chat(roomId);
    }
}
