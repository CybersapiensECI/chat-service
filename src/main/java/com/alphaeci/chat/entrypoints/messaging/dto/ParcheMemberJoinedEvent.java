package com.alphaeci.chat.entrypoints.messaging.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Publicado por Parches-Service (notification.exchange / parche.member-joined) al unirse un miembro. */
@Getter
@NoArgsConstructor
public class ParcheMemberJoinedEvent {
    private UUID parcheId;
    private UUID studentId;
}
