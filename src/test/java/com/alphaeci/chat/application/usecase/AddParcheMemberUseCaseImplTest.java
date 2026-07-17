package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddParcheMemberUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private AddParcheMemberUseCaseImpl useCase;

    @Test
    void execute_addsMemberToExistingRoom() {
        UUID parcheId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID newMemberId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(parcheId)
                .parcheId(parcheId)
                .memberIds(new ArrayList<>(List.of(creatorId)))
                .build();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.of(room));

        useCase.execute(parcheId, newMemberId);

        verify(chatRoomRepository).save(argThat(r ->
                r.getMemberIds().contains(creatorId) && r.getMemberIds().contains(newMemberId)
        ));
    }

    @Test
    void execute_roomNotFound_doesNothing() {
        UUID parcheId = UUID.randomUUID();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.empty());

        useCase.execute(parcheId, UUID.randomUUID());

        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    void execute_alreadyMember_doesNotSaveAgain() {
        UUID parcheId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(parcheId)
                .memberIds(new ArrayList<>(List.of(memberId)))
                .build();
        when(chatRoomRepository.findById(parcheId)).thenReturn(Optional.of(room));

        useCase.execute(parcheId, memberId);

        verify(chatRoomRepository, never()).save(any());
    }
}
