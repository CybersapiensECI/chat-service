package com.alphaeci.chat.entrypoints.rest.controller;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.application.usecase.CreateFriendshipRoomUseCaseImpl;
import com.alphaeci.chat.application.usecase.EnsureParcheRoomUseCaseImpl;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.ports.in.GetConnectionsUseCase;
import com.alphaeci.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.in.SendConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.out.ProfileServicePort;
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
    private final CreateFriendshipRoomUseCaseImpl createFriendshipRoomUseCase;
    private final EnsureParcheRoomUseCaseImpl ensureParcheRoomUseCase;
    private final ChatMapper chatMapper;
    private final ProfileServicePort profileServicePort;

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
            summary = "Get or create the direct chat room with a friend",
            description = "Idempotent: returns the existing ACTIVE room between the two users, creating it if "
                    + "it doesn't exist yet. Meant for friendships that predate the friendship.created event "
                    + "consumer (or any case where that event was missed) — the frontend calls this on demand "
                    + "when the user taps \"Mensaje\" on a friend, so old friendships self-heal instead of "
                    + "needing a data migration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found or created"),
            @ApiResponse(responseCode = "403", description = "userId and friendId are not actually friends")
    })
    @PostMapping("/friend/{friendId}")
    public ResponseEntity<ConnectionResponse> ensureFriendRoom(
            @Parameter(description = "Authenticated user, propagated by the API Gateway", required = true,
                    example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "The friend to open/create a direct chat with", required = true,
                    example = "9c8b7a6d-5e4f-43c2-b1a0-9f8e7d6c5b4a")
            @PathVariable UUID friendId) {
        // X-User-Id no prueba ninguna relación por sí solo — sin esto,
        // cualquier usuario autenticado podía abrir chat directo con
        // cualquier otro id sin ser amigos de verdad.
        if (!profileServicePort.areFriends(userId, friendId)) {
            throw new UserNotMemberException(
                    "User " + userId + " is not friends with " + friendId);
        }
        var room = createFriendshipRoomUseCase.execute(userId, friendId);
        return ResponseEntity.ok(chatMapper.toConnectionResponse(room, userId));
    }

    @Operation(
            summary = "Ensure the group chat room of a parche exists (with the caller as member)",
            description = "Idempotent get-or-create: the room id equals the parcheId. Meant for parches that "
                    + "predate the parche.created event consumer (or whose events were missed) — the frontend "
                    + "calls this before opening a parche chat, so existing parches self-heal instead of "
                    + "needing a data migration. If the room already exists it only adds the caller as a "
                    + "member when missing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room ensured")
    })
    @PostMapping("/parche/{parcheId}")
    public ResponseEntity<Void> ensureParcheRoom(
            @Parameter(description = "Authenticated user, propagated by the API Gateway", required = true,
                    example = "3f2a7c1e-9b4d-4c8a-b0f6-2d1e5a7c9b03")
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "The parche whose group chat room must exist", required = true,
                    example = "7b6a5c4d-3e2f-41a0-9b8c-7d6e5f4a3b2c")
            @PathVariable UUID parcheId) {
        ensureParcheRoomUseCase.execute(parcheId, userId);
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
