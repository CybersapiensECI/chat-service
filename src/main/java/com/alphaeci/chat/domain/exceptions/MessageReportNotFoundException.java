package com.alphaeci.chat.domain.exceptions;

import java.util.UUID;

public class MessageReportNotFoundException extends RuntimeException {
    public MessageReportNotFoundException(UUID id) {
        super("Message report not found: " + id);
    }
}
