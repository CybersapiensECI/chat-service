package com.alphaeci.chat.domain.ports.out;

import com.alphaeci.chat.domain.model.Message;

public interface ChatEventPublisher {
    void publishMessageSent(Message message, String senderId);
}
