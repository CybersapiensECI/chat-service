package com.alphaeci.chat.infrastructure.adapters.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMongoRepository extends MongoRepository<ChatRoomDocument, String> {
    Optional<ChatRoomDocument> findByParcheId(String parcheId);
    List<ChatRoomDocument> findAllByMemberIdsContaining(String userId);
}
