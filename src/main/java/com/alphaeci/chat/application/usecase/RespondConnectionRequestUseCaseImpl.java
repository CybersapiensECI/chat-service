package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RespondConnectionRequestUseCaseImpl implements RespondConnectionRequestUseCase {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void execute(UUID userId, UUID requesterId, boolean accepted) {
        var chatRoom = chatRoomRepository.findAllByMemberId(userId).stream()
                .filter(r -> r.getRequesterId().equals(requesterId) && r.getStatus() == ChatRoomStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new ChatRoomNotFoundException(requesterId));

        ChatRoomStatus newStatus = accepted ? ChatRoomStatus.ACTIVE : ChatRoomStatus.REJECTED;

        chatRoomRepository.save(chatRoom.toBuilder().status(newStatus).build());
    }
}
