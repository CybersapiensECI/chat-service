package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID chatRoomId;
    private UUID senderId;
    private String content;
    private String mediaUrl;
    private MessageType type;
    private boolean deleted;
    private LocalDateTime sentAt;
}
