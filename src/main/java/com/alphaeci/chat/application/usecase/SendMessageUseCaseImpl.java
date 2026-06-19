package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.request.SendMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.exceptions.InvalidMessageException;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.enums.MessageType;
import com.alphaeci.chat.domain.ports.in.SendMessageUseCase;
import com.alphaeci.chat.domain.ports.out.ChatEventPublisher;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendMessageUseCaseImpl implements SendMessageUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ChatEventPublisher chatEventPublisher;
    private final ChatMapper chatMapper;

    @Override
    public MessageResponse execute(UUID userId, UUID chatRoomId, SendMessageRequest request) {
        var chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId));

        if (!chatRoom.isMember(userId)) {
            throw new UserNotMemberException(userId, chatRoomId);
        }

        if (request.getType() == MessageType.TEXT && (request.getContent() == null || request.getContent().isBlank())) {
            throw new InvalidMessageException("Text message must have content");
        }

        if (request.getType() == MessageType.IMAGE && (request.getMediaUrl() == null || request.getMediaUrl().isBlank())) {
            throw new InvalidMessageException("Image message must have a media URL");
        }

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .type(request.getType())
                .deleted(false)
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);
        chatEventPublisher.publishMessageSent(saved, userId.toString());

        return chatMapper.toMessageResponse(saved);
    }
}
