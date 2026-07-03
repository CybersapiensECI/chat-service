package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendConnectionRequestUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;

    @InjectMocks private SendConnectionRequestUseCaseImpl useCase;

    @Test
    void execute_createsPendingChatRoom() {
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        useCase.execute(requesterId, targetId);

        verify(chatRoomRepository).save(argThat(room ->
                room.getStatus() == ChatRoomStatus.PENDING &&
                room.getRequesterId().equals(requesterId) &&
                room.getMemberIds().contains(requesterId) &&
                room.getMemberIds().contains(targetId)
        ));
    }
}
