package com.alphaeci.chat.domain.ports.in;

import java.util.UUID;

public interface SendConnectionRequestUseCase {
    void execute(UUID requesterId, UUID targetId);
}
