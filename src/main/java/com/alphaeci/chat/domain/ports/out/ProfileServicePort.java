package com.alphaeci.chat.domain.ports.out;

import java.util.UUID;

/**
 * Verifica relaciones reales antes de crear una sala directa — sin esto,
 * cualquier usuario autenticado podía abrir chat con cualquier otro id sin
 * ser amigos de verdad (X-User-Id no prueba ninguna relación por sí solo).
 */
public interface ProfileServicePort {
    boolean areFriends(UUID userId, UUID otherUserId);
}
