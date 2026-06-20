package com.alphaeci.chat.entrypoints.websocket;

import com.alphaeci.chat.application.dto.request.SendMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.domain.ports.in.SendMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketHandler {

    private final SendMessageUseCase sendMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatRoomId}/send")
    public void sendMessage(
            @DestinationVariable UUID chatRoomId,
            @Header("X-User-Id") UUID userId,
            @Payload SendMessageRequest request) {
        MessageResponse response = sendMessageUseCase.execute(userId, chatRoomId, request);
        messagingTemplate.convertAndSend("/topic/parche/" + chatRoomId + "/messages", response);
    }
}
