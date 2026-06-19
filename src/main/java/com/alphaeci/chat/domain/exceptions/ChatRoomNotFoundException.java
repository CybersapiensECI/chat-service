package com.alphaeci.chat.domain.exceptions;

import java.util.UUID;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException(UUID id) {
        super("Chat room not found: " + id);
    }
}
