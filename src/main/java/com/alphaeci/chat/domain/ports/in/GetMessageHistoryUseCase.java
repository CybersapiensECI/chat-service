package com.alphaeci.chat.domain.ports.in;

import com.alphaeci.chat.application.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetMessageHistoryUseCase {
    Page<MessageResponse> execute(UUID chatRoomId, UUID userId, Pageable pageable);
}
