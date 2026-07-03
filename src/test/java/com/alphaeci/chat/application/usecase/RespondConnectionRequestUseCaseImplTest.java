package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespondConnectionRequestUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private RespondConnectionRequestUseCaseImpl useCase;

    private UUID userId;
    private UUID requesterId;
    private ChatRoom pendingRoom;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        requesterId = UUID.randomUUID();
        pendingRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(requesterId)
                .status(ChatRoomStatus.PENDING)
                .memberIds(new ArrayList<>(List.of(requesterId, userId)))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_accepted_setsActiveStatus() {
        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(pendingRoom));

        useCase.execute(userId, requesterId, true);

        verify(chatRoomRepository).save(argThat(r -> r.getStatus() == ChatRoomStatus.ACTIVE));
    }

    @Test
    void execute_rejected_setsRejectedStatus() {
        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(pendingRoom));

        useCase.execute(userId, requesterId, false);

        verify(chatRoomRepository).save(argThat(r -> r.getStatus() == ChatRoomStatus.REJECTED));
    }

    @Test
    void execute_noPendingRoom_throwsException() {
        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of());

        assertThrows(ChatRoomNotFoundException.class, () -> useCase.execute(userId, requesterId, true));
    }
}
