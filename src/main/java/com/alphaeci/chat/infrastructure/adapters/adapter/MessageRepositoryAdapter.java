package com.alphaeci.chat.infrastructure.adapters.adapter;

import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.MessageMongoRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepository {

    private final MessageMongoRepository mongoRepository;
    private final PersistenceMapper mapper;

    @Override
    public Message save(Message message) {
        return mapper.toMessage(mongoRepository.save(mapper.toMessageDocument(message)));
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return mongoRepository.findById(id.toString()).map(mapper::toMessage);
    }

    @Override
    public Page<Message> findAllByChatRoomId(UUID chatRoomId, Pageable pageable) {
        return mongoRepository.findAllByChatRoomId(chatRoomId.toString(), pageable)
                .map(mapper::toMessage);
    }
}
