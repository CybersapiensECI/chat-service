package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFriendshipRoomUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private CreateFriendshipRoomUseCaseImpl useCase;

    @Test
    void execute_createsActiveRoomWithBothMembers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        useCase.execute(userId1, userId2);

        verify(chatRoomRepository).save(argThat(room ->
                room.getStatus() == ChatRoomStatus.ACTIVE &&
                room.getMemberIds().contains(userId1) &&
                room.getMemberIds().contains(userId2) &&
                room.getMemberIds().size() == 2
        ));
    }

    @Test
    void execute_sameIdRegardlessOfArgumentOrder() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        useCase.execute(userId1, userId2);
        UUID firstOrderId = captureRoomId();

        reset(chatRoomRepository);
        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());
        useCase.execute(userId2, userId1);
        UUID secondOrderId = captureRoomId();

        assertEquals(firstOrderId, secondOrderId);
    }

    @Test
    void execute_roomAlreadyExists_doesNotCreateDuplicate() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(ChatRoom.builder().build()));

        useCase.execute(userId1, userId2);

        verify(chatRoomRepository, never()).save(any());
    }

    private UUID captureRoomId() {
        var captor = org.mockito.ArgumentCaptor.forClass(ChatRoom.class);
        verify(chatRoomRepository).save(captor.capture());
        return captor.getValue().getId();
    }
}
