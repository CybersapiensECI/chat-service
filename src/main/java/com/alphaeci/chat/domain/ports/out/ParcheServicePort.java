package com.alphaeci.chat.domain.ports.out;

import java.util.UUID;

/**
 * Verifica membresía real de parche antes de sumar a alguien al chat
 * grupal — sin esto, cualquier usuario autenticado podía entrar al chat de
 * un parche sin ser miembro de ese parche.
 */
public interface ParcheServicePort {
    boolean isMember(UUID parcheId, UUID userId);
}
