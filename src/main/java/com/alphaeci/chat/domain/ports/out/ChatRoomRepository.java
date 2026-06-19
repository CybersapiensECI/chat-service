package com.alphaeci.chat.domain.ports.out;

import com.alphaeci.chat.domain.model.ChatRoom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(UUID id);
    Optional<ChatRoom> findByParcheId(UUID parcheId);
    List<ChatRoom> findAllByMemberId(UUID userId);
}
