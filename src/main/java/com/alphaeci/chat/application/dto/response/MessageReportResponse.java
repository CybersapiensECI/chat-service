package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.ReportStatus;
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
@Schema(description = "A moderation report and its review state")
public class MessageReportResponse {

    @Schema(description = "Report identifier", example = "1a2b3c4d-5e6f-4708-9a0b-1c2d3e4f5a6b")
    private UUID id;

    @Schema(description = "Message that was reported", example = "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b")
    private UUID messageId;

    @Schema(description = "User who filed the report", example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
    private UUID reporterId;

    @Schema(description = "Why the message was reported", example = "Contenido ofensivo")
    private String reason;

    @Schema(description = "Review state of the report", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "When the report was filed", example = "2026-04-15T10:35:00")
    private LocalDateTime createdAt;
}
