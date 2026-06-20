package com.alphaeci.chat.infrastructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "message_reports")
public class MessageReportDocument {
    @Id
    private String id;
    private String messageId;
    private String reporterId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}
