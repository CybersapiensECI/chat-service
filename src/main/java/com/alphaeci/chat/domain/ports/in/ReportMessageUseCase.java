package com.alphaeci.chat.domain.ports.in;

import com.alphaeci.chat.application.dto.request.ReportMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;

import java.util.UUID;

public interface ReportMessageUseCase {
    MessageReportResponse execute(UUID reporterId, ReportMessageRequest request);
}
