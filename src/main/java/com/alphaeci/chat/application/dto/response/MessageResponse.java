package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.MessageType;
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
@Schema(description = "A message stored in a chat room")
public class MessageResponse {

    @Schema(description = "Message identifier", example = "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b")
    private UUID id;

    @Schema(description = "Chat room the message belongs to", example = "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c")
    private UUID chatRoomId;

    @Schema(description = "Author of the message", example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
    private UUID senderId;

    @Schema(description = "Text body; null for image-only messages", example = "¿A qué hora nos vemos?")
    private String content;

    @Schema(description = "Media URL; null for text-only messages",
            example = "https://cdn.patricia.app/media/parche-42.jpg")
    private String mediaUrl;

    @Schema(description = "Message kind", example = "TEXT")
    private MessageType type;

    @Schema(description = "Whether the message was soft-deleted", example = "false")
    private boolean deleted;

    @Schema(description = "Send timestamp", example = "2026-04-15T10:30:00")
    private LocalDateTime sentAt;
}
