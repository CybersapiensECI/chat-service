package com.alphaeci.chat.domain.ports.in;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;

import java.util.List;
import java.util.UUID;

public interface GetConnectionsUseCase {
    List<ConnectionResponse> execute(UUID userId);
}
