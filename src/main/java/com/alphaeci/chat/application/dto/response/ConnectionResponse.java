package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "A chat room the user belongs to, seen from that user's side")
public class ConnectionResponse {

    @Schema(description = "Chat room identifier", example = "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c")
    private UUID chatRoomId;

    @Schema(description = "The other member of the room", example = "9c8b7a6d-5e4f-43c2-b1a0-9f8e7d6c5b4a")
    private UUID otherUserId;

    @Schema(description = "Connection state", example = "ACTIVE")
    private ChatRoomStatus status;

    @Schema(description = "When the connection was requested", example = "2026-04-14T09:00:00")
    private LocalDateTime createdAt;
}
