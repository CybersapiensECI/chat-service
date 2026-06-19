package com.alphaeci.chat.domain.model;

import com.alphaeci.chat.domain.model.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageReport {
    private UUID id;
    private UUID messageId;
    private UUID reporterId;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
