package com.alphaeci.chat.domain.ports.in;

import java.util.UUID;

public interface RespondConnectionRequestUseCase {
    void execute(UUID userId, UUID requesterId, boolean accepted);
}
