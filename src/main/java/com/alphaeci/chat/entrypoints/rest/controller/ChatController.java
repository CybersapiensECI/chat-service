package com.alphaeci.chat.entrypoints.rest.controller;

import com.alphaeci.chat.application.dto.request.ReportMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.alphaeci.chat.domain.ports.in.ReportMessageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat — Messages", description = "Message history and content moderation")
public class ChatController {

    private final GetMessageHistoryUseCase getMessageHistoryUseCase;
    private final ReportMessageUseCase reportMessageUseCase;

    @Operation(
            summary = "Get a chat room's message history",
            description = """
                    Returns the messages of a chat room, paginated. The authenticated user must be a
                    member of the room.

                    Messages are sent over WebSocket, not through this API — publish to
                    /app/chat/{chatRoomId}/send and subscribe to /topic/parche/{chatRoomId}/messages.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of messages"),
            @ApiResponse(responseCode = "403", description = "The user is not a member of the chat room"),
            @ApiResponse(responseCode = "404", description = "Chat room not found")
    })
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<Page<MessageResponse>> getHistory(
            @Parameter(description = "Chat room to read", required = true,
                    example = "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c")
            @PathVariable UUID chatRoomId,
            @Parameter(description = "Authenticated user, propagated by the API Gateway", required = true,
                    example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(getMessageHistoryUseCase.execute(chatRoomId, userId, pageable));
    }

    @Operation(
            summary = "Report a message",
            description = "Files a moderation report against a message. The report is created with status PENDING.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Report created"),
            @ApiResponse(responseCode = "400", description = "Missing messageId or blank/oversized reason"),
            @ApiResponse(responseCode = "404", description = "The reported message does not exist")
    })
    @PostMapping("/messages/report")
    public ResponseEntity<MessageReportResponse> report(
            @Parameter(description = "Authenticated user, propagated by the API Gateway", required = true,
                    example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody ReportMessageRequest request) {
        return ResponseEntity.status(201).body(reportMessageUseCase.execute(userId, request));
    }
}
