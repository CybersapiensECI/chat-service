package com.alphaeci.chat.infrastructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class MessageDocument {
    @Id
    private String id;
    @Indexed
    private String chatRoomId;
    private String senderId;
    private String content;
    private String mediaUrl;
    private String type;
    private boolean deleted;
    private LocalDateTime sentAt;
}
