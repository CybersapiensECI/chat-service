package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.in.SendConnectionRequestUseCase;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendConnectionRequestUseCaseImpl implements SendConnectionRequestUseCase {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void execute(UUID requesterId, UUID targetId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(requesterId)
                .status(ChatRoomStatus.PENDING)
                .memberIds(new ArrayList<>(List.of(requesterId, targetId)))
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);
    }
}
