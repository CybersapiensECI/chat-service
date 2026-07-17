package com.alphaeci.chat.entrypoints.messaging.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Publicado por Parches-Service (notification.exchange / parche.created) al crear un parche. */
@Getter
@NoArgsConstructor
public class ParcheCreatedEvent {
    private UUID parcheId;
    private UUID creatorId;
}
