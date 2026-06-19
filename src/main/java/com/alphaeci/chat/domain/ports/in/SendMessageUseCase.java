package com.alphaeci.chat.domain.ports.in;

import com.alphaeci.chat.application.dto.request.SendMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageResponse;

import java.util.UUID;

public interface SendMessageUseCase {
    MessageResponse execute(UUID userId, UUID chatRoomId, SendMessageRequest request);
}
