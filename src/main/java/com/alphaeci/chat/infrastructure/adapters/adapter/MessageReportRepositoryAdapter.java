package com.alphaeci.chat.infrastructure.adapters.adapter;

import com.alphaeci.chat.domain.model.MessageReport;
import com.alphaeci.chat.domain.ports.out.MessageReportRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.MessageReportMongoRepository;
import com.alphaeci.chat.infrastructure.adapters.persistence.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageReportRepositoryAdapter implements MessageReportRepository {

    private final MessageReportMongoRepository mongoRepository;
    private final PersistenceMapper mapper;

    @Override
    public MessageReport save(MessageReport report) {
        return mapper.toMessageReport(mongoRepository.save(mapper.toMessageReportDocument(report)));
    }

    @Override
    public Optional<MessageReport> findById(UUID id) {
        return mongoRepository.findById(id.toString()).map(mapper::toMessageReport);
    }
}
