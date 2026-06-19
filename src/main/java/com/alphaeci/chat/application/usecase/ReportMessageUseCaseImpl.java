package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.request.ReportMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.MessageNotFoundException;
import com.alphaeci.chat.domain.model.MessageReport;
import com.alphaeci.chat.domain.model.enums.ReportStatus;
import com.alphaeci.chat.domain.ports.in.ReportMessageUseCase;
import com.alphaeci.chat.domain.ports.out.MessageReportRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportMessageUseCaseImpl implements ReportMessageUseCase {

    private final MessageRepository messageRepository;
    private final MessageReportRepository messageReportRepository;
    private final ChatMapper chatMapper;

    @Override
    public MessageReportResponse execute(UUID reporterId, ReportMessageRequest request) {
        messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new MessageNotFoundException(request.getMessageId()));

        MessageReport report = MessageReport.builder()
                .id(UUID.randomUUID())
                .messageId(request.getMessageId())
                .reporterId(reporterId)
                .reason(request.getReason())
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return chatMapper.toMessageReportResponse(messageReportRepository.save(report));
    }
}
