package com.alphaeci.chat.entrypoints.rest.controller;

import com.alphaeci.chat.application.dto.request.ReportMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.alphaeci.chat.domain.ports.in.ReportMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final GetMessageHistoryUseCase getMessageHistoryUseCase;
    private final ReportMessageUseCase reportMessageUseCase;

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<Page<MessageResponse>> getHistory(
            @PathVariable UUID chatRoomId,
            @RequestHeader("X-User-Id") UUID userId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(getMessageHistoryUseCase.execute(chatRoomId, userId, pageable));
    }

    @PostMapping("/messages/report")
    public ResponseEntity<MessageReportResponse> report(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody ReportMessageRequest request) {
        return ResponseEntity.status(201).body(reportMessageUseCase.execute(userId, request));
    }
}
