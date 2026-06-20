package com.alphaeci.chat.infrastructure.adapters.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageReportMongoRepository extends MongoRepository<MessageReportDocument, String> {
}
