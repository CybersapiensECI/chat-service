package com.alphaeci.chat.infrastructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_rooms")
public class ChatRoomDocument {
    @Id
    private String id;
    @Indexed
    private String parcheId;
    private String requesterId;
    private String status;
    private List<String> memberIds;
    private LocalDateTime createdAt;
}
