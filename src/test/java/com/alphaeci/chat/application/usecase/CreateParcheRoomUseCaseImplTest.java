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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateParcheRoomUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private CreateParcheRoomUseCaseImpl useCase;

    @Test
    void execute_createsActiveRoomWithIdEqualToParcheId() {
        UUID parcheId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.empty());

        useCase.execute(parcheId, creatorId);

        verify(chatRoomRepository).save(argThat(room ->
                room.getId().equals(parcheId) &&
                room.getParcheId().equals(parcheId) &&
                room.getStatus() == ChatRoomStatus.ACTIVE &&
                room.getMemberIds().contains(creatorId)
        ));
    }

    @Test
    void execute_roomAlreadyExists_doesNotCreateDuplicate() {
        UUID parcheId = UUID.randomUUID();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.of(ChatRoom.builder().build()));

        useCase.execute(parcheId, UUID.randomUUID());

        verify(chatRoomRepository, never()).save(any());
    }
}
