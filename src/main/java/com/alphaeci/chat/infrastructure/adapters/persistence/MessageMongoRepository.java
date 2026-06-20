package com.alphaeci.chat.infrastructure.adapters.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {
    Page<MessageDocument> findAllByChatRoomId(String chatRoomId, Pageable pageable);
}
