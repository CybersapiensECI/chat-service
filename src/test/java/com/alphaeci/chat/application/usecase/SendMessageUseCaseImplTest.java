package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.request.SendMessageRequest;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.exceptions.InvalidMessageException;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.model.enums.MessageType;
import com.alphaeci.chat.domain.ports.out.ChatEventPublisher;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMessageUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ChatEventPublisher chatEventPublisher;
    @Mock private ChatMapper chatMapper;

    @InjectMocks private SendMessageUseCaseImpl useCase;

    private UUID userId;
    private UUID chatRoomId;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .requesterId(UUID.randomUUID())
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId)))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_validTextMessage_savesAndPublishes() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.TEXT)
                .content("Hola!")
                .build();

        Message saved = Message.builder()
                .id(UUID.randomUUID())
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content("Hola!")
                .type(MessageType.TEXT)
                .deleted(false)
                .sentAt(LocalDateTime.now())
                .build();

        MessageResponse response = MessageResponse.builder()
                .id(saved.getId())
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content("Hola!")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(messageRepository.save(any())).thenReturn(saved);
        when(chatMapper.toMessageResponse(saved)).thenReturn(response);

        MessageResponse result = useCase.execute(userId, chatRoomId, request);

        assertNotNull(result);
        verify(messageRepository).save(any());
        verify(chatEventPublisher).publishMessageSent(any(), eq(userId.toString()));
    }

    @Test
    void execute_chatRoomNotFound_throwsException() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.TEXT)
                .content("Hola!")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        assertThrows(ChatRoomNotFoundException.class, () -> useCase.execute(userId, chatRoomId, request));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void execute_userNotMember_throwsException() {
        UUID outsider = UUID.randomUUID();
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.TEXT)
                .content("Hola!")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        assertThrows(UserNotMemberException.class, () -> useCase.execute(outsider, chatRoomId, request));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void execute_textMessageEmptyContent_throwsException() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.TEXT)
                .content("")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        assertThrows(InvalidMessageException.class, () -> useCase.execute(userId, chatRoomId, request));
    }

    @Test
    void execute_imageMessageNoUrl_throwsException() {
        SendMessageRequest request = SendMessageRequest.builder()
                .type(MessageType.IMAGE)
                .mediaUrl(null)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        assertThrows(InvalidMessageException.class, () -> useCase.execute(userId, chatRoomId, request));
    }
}
