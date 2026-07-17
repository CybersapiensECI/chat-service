package com.alphaeci.chat.entrypoints.rest.controller;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.domain.ports.in.GetConnectionsUseCase;
import com.alphaeci.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.in.SendConnectionRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/connections")
@RequiredArgsConstructor
@Tag(name = "Chat — Connections", description = "Connection requests between users and their chat rooms")
public class ConnectionController {

    private final SendConnectionRequestUseCase sendConnectionRequestUseCase;
    private final RespondConnectionRequestUseCase respondConnectionRequestUseCase;
    private final GetConnectionsUseCase getConnectionsUseCase;

    @Operation(
            summary = "Send a connection request",
            description = "Creates a chat room in PENDING status with the requester and the target user as members.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Connection request created"),
            @ApiResponse(responseCode = "400", description = "Missing targetId or X-User-Id header")
    })
    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(
            @Parameter(description = "Authenticated user sending the request, propagated by the API Gateway",
                    required = true, example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "User being invited to connect", required = true,
                    example = "9c8b7a6d-5e4f-43c2-b1a0-9f8e7d6c5b4a")
            @RequestParam UUID targetId) {
        sendConnectionRequestUseCase.execute(userId, targetId);
        return ResponseEntity.status(201).build();
    }

    @Operation(
            summary = "Accept or reject a connection request",
            description = """
                    Resolves the PENDING chat room opened by `requesterId`. The room becomes ACTIVE when
                    accepted is true, REJECTED otherwise.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request resolved"),
            @ApiResponse(responseCode = "404", description = "No pending request from that user")
    })
    @PostMapping("/respond")
    public ResponseEntity<Void> respond(
            @Parameter(description = "Authenticated user answering the request, propagated by the API Gateway",
                    required = true, example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "User who originally sent the request", required = true,
                    example = "9c8b7a6d-5e4f-43c2-b1a0-9f8e7d6c5b4a")
            @RequestParam UUID requesterId,
            @Parameter(description = "True to accept (ACTIVE), false to reject (REJECTED)", required = true,
                    example = "true")
            @RequestParam boolean accepted) {
        respondConnectionRequestUseCase.execute(userId, requesterId, accepted);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "List the user's connections",
            description = "Returns every chat room the authenticated user belongs to, in any status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of connections"),
            @ApiResponse(responseCode = "400", description = "Missing or malformed X-User-Id header")
    })
    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            @Parameter(description = "Authenticated user, propagated by the API Gateway", required = true,
                    example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(getConnectionsUseCase.execute(userId));
    }
}
