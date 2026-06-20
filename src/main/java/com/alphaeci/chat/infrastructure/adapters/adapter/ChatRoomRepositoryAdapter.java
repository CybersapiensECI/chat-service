package com.alphaeci.chat.infrastructure.adapters.adapter;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.ChatRoomMongoRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatRoomRepositoryAdapter implements ChatRoomRepository {

    private final ChatRoomMongoRepository mongoRepository;
    private final PersistenceMapper mapper;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return mapper.toChatRoom(mongoRepository.save(mapper.toChatRoomDocument(chatRoom)));
    }

    @Override
    public Optional<ChatRoom> findById(UUID id) {
        return mongoRepository.findById(id.toString()).map(mapper::toChatRoom);
    }

    @Override
    public Optional<ChatRoom> findByParcheId(UUID parcheId) {
        return mongoRepository.findByParcheId(parcheId.toString()).map(mapper::toChatRoom);
    }

    @Override
    public List<ChatRoom> findAllByMemberId(UUID userId) {
        return mongoRepository.findAllByMemberIdsContaining(userId.toString())
                .stream().map(mapper::toChatRoom).toList();
    }
}
