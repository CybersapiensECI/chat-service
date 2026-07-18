package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnsureParcheRoomUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private EnsureParcheRoomUseCaseImpl useCase;

    @Test
    void execute_roomMissing_createsActiveRoomWithCallerAsMember() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.empty());

        useCase.execute(parcheId, userId);

        verify(chatRoomRepository).save(argThat(room ->
                room.getId().equals(parcheId) &&
                room.getParcheId().equals(parcheId) &&
                room.getStatus() == ChatRoomStatus.ACTIVE &&
                room.getMemberIds().contains(userId)
        ));
    }

    @Test
    void execute_roomExistsWithoutCaller_addsCallerAsMember() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(parcheId)
                .parcheId(parcheId)
                .memberIds(new ArrayList<>(List.of(UUID.randomUUID())))
                .build();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.of(room));

        useCase.execute(parcheId, userId);

        verify(chatRoomRepository).save(argThat(saved -> saved.getMemberIds().contains(userId)));
    }

    @Test
    void execute_roomExistsWithCaller_savesNothing() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(parcheId)
                .parcheId(parcheId)
                .memberIds(new ArrayList<>(List.of(userId)))
                .build();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.of(room));

        ChatRoom result = useCase.execute(parcheId, userId);

        assertSame(room, result);
        verify(chatRoomRepository, never()).save(any());
    }
}
