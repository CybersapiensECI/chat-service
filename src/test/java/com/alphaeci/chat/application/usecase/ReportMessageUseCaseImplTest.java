package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.request.ReportMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.MessageNotFoundException;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.MessageReport;
import com.alphaeci.chat.domain.model.enums.MessageType;
import com.alphaeci.chat.domain.model.enums.ReportStatus;
import com.alphaeci.chat.domain.ports.out.MessageReportRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportMessageUseCaseImplTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageReportRepository messageReportRepository;
    @Mock private ChatMapper chatMapper;

    @InjectMocks private ReportMessageUseCaseImpl useCase;

    @Test
    void execute_validReport_savesAndReturns() {
        UUID reporterId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        ReportMessageRequest request = ReportMessageRequest.builder()
                .messageId(messageId)
                .reason("Contenido inapropiado")
                .build();

        Message message = Message.builder()
                .id(messageId)
                .type(MessageType.TEXT)
                .content("mensaje")
                .sentAt(LocalDateTime.now())
                .build();

        MessageReport saved = MessageReport.builder()
                .id(UUID.randomUUID())
                .messageId(messageId)
                .reporterId(reporterId)
                .reason("Contenido inapropiado")
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        MessageReportResponse response = MessageReportResponse.builder()
                .id(saved.getId())
                .messageId(messageId)
                .status(ReportStatus.PENDING)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageReportRepository.save(any())).thenReturn(saved);
        when(chatMapper.toMessageReportResponse(saved)).thenReturn(response);

        MessageReportResponse result = useCase.execute(reporterId, request);

        assertNotNull(result);
        assertEquals(ReportStatus.PENDING, result.getStatus());
        verify(messageReportRepository).save(any());
    }

    @Test
    void execute_messageNotFound_throwsException() {
        UUID messageId = UUID.randomUUID();
        ReportMessageRequest request = ReportMessageRequest.builder()
                .messageId(messageId)
                .reason("Spam")
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> useCase.execute(UUID.randomUUID(), request));
        verify(messageReportRepository, never()).save(any());
    }
}
