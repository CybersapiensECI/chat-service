package com.alphaeci.chat.application.dto.response;

import com.alphaeci.chat.domain.model.enums.ReportStatus;
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
public class MessageReportResponse {
    private UUID id;
    private UUID messageId;
    private UUID reporterId;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
