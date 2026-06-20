package com.alphaeci.chat.entrypoints.rest.controller;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.domain.ports.in.GetConnectionsUseCase;
import com.alphaeci.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.in.SendConnectionRequestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final SendConnectionRequestUseCase sendConnectionRequestUseCase;
    private final RespondConnectionRequestUseCase respondConnectionRequestUseCase;
    private final GetConnectionsUseCase getConnectionsUseCase;

    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam UUID targetId) {
        sendConnectionRequestUseCase.execute(userId, targetId);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/respond")
    public ResponseEntity<Void> respond(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam UUID requesterId,
            @RequestParam boolean accepted) {
        respondConnectionRequestUseCase.execute(userId, requesterId, accepted);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(getConnectionsUseCase.execute(userId));
    }
}
