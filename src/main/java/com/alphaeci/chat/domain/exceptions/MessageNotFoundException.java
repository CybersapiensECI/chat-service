package com.alphaeci.chat.domain.exceptions;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(UUID id) {
        super("Message not found: " + id);
    }
}
