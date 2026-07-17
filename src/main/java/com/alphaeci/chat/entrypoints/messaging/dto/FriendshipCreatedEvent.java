package com.alphaeci.chat.entrypoints.messaging.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Publicado por profile-service (notification.exchange / friendship.created) cuando dos usuarios quedan amigos. */
@Getter
@NoArgsConstructor
public class FriendshipCreatedEvent {
    private UUID userId1;
    private UUID userId2;
    private LocalDateTime createdAt;
}
