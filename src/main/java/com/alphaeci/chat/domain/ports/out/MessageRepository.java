package com.alphaeci.chat.domain.ports.out;

import com.alphaeci.chat.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    Page<Message> findAllByChatRoomId(UUID chatRoomId, Pageable pageable);
}
