package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sala grupal de un parche: id == parcheId a propósito, así el frontend
 * (que ya usa parche.id como chatRoomId) encuentra la sala sin ningún
 * cambio de contrato ni una llamada de lookup extra.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateParcheRoomUseCaseImpl {

    private final ChatRoomRepository chatRoomRepository;

    public void execute(UUID parcheId, UUID creatorId) {
        if (chatRoomRepository.findById(parcheId).isPresent()) {
            log.info("Parche room {} already exists, skipping", parcheId);
            return;
        }

        List<UUID> members = new ArrayList<>(List.of(creatorId));
        ChatRoom chatRoom = ChatRoom.builder()
                .id(parcheId)
                .parcheId(parcheId)
                .requesterId(creatorId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(members)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);
        log.info("Created parche chat room {} (creator {})", parcheId, creatorId);
    }
}
