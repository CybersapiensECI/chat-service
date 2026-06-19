package com.alphaeci.chat.domain.ports.out;

import com.alphaeci.chat.domain.model.MessageReport;

import java.util.Optional;
import java.util.UUID;

public interface MessageReportRepository {
    MessageReport save(MessageReport report);
    Optional<MessageReport> findById(UUID id);
}
