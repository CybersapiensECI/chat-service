package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
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
public class ConnectionResponse {
    private UUID chatRoomId;
    private UUID otherUserId;
    private ChatRoomStatus status;
    private LocalDateTime createdAt;
}
