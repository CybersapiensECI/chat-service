package com.alphaeci.chat.entrypoints.messaging.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    private UUID chatId;
    /** UUID del remitente (nunca fue un nombre — ver publishMessageSent). */
    private String senderId;
    private String content;
}
