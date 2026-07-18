package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.ParcheServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Get-or-create de la sala grupal de un parche, con el caller como miembro.
 *
 * Mismo espíritu que CreateFriendshipRoomUseCaseImpl#execute vía
 * ConnectionController.ensureFriendRoom: Parches-Service todavía no publica
 * parche.created / parche.member-joined (no tiene RabbitMQ), así que las
 * salas de parche nunca nacen por eventos. En vez de exigir esa pieza para
 * que el chat funcione, el frontend llama este ensure al abrir el chat del
 * parche y la sala (y la membresía del que entra) se auto-repara al vuelo.
 * Cuando Parches-Service publique eventos, esto queda como red de seguridad
 * idempotente: si la sala ya existe solo agrega al miembro que falte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnsureParcheRoomUseCaseImpl {

    private final ChatRoomRepository chatRoomRepository;
    private final ParcheServicePort parcheServicePort;

    public ChatRoom execute(UUID parcheId, UUID userId) {
        // X-User-Id no prueba membresía del parche por sí solo — sin esto,
        // cualquier usuario autenticado podía entrar al chat de un parche
        // sin ser miembro de ese parche.
        if (!parcheServicePort.isMember(parcheId, userId)) {
            throw new UserNotMemberException(
                    "User " + userId + " is not a member of parche " + parcheId);
        }
        var existing = chatRoomRepository.findById(parcheId).orElse(null);
        if (existing == null) {
            List<UUID> members = new ArrayList<>(List.of(userId));
            ChatRoom room = ChatRoom.builder()
                    .id(parcheId)
                    .parcheId(parcheId)
                    .requesterId(userId)
                    .status(ChatRoomStatus.ACTIVE)
                    .memberIds(members)
                    .createdAt(LocalDateTime.now())
                    .build();
            ChatRoom saved = chatRoomRepository.save(room);
            log.info("Ensured parche room {} (created on demand by {})", parcheId, userId);
            return saved;
        }
        if (!existing.isMember(userId)) {
            existing.addMember(userId);
            ChatRoom saved = chatRoomRepository.save(existing);
            log.info("Ensured parche room {}: added missing member {}", parcheId, userId);
            return saved;
        }
        return existing;
    }
}
