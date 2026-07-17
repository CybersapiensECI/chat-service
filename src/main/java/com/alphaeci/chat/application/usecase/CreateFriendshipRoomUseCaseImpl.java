package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Crea directo el chat ACTIVE entre dos amigos nuevos — a diferencia del
 * flujo manual de ConnectionController (PENDING -> respond), aquí el
 * "consentimiento mutuo" ya ocurrió al aceptar el match en matching-service,
 * así que no tiene sentido pedir una segunda confirmación solo para chatear.
 *
 * Id determinístico (hash de los dos userId ordenados) para que reintentos
 * o reentregas del mismo evento no dupliquen la sala: save() sobre el mismo
 * id es un upsert. También se usa como get-or-create bajo demanda (ver
 * ConnectionController.ensureFriendRoom): backfill natural para amistades
 * que ya existían antes de que este consumer existiera, sin necesitar una
 * migración aparte — la primera vez que alguien toca "Mensaje" con un
 * amigo viejo, la sala aparece sola.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateFriendshipRoomUseCaseImpl {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom execute(UUID userId1, UUID userId2) {
        UUID roomId = deterministicRoomId(userId1, userId2);
        var existing = chatRoomRepository.findById(roomId);
        if (existing.isPresent()) {
            log.info("Friendship room {} already exists for {}/{}, skipping", roomId, userId1, userId2);
            return existing.get();
        }

        List<UUID> members = new ArrayList<>(List.of(userId1, userId2));
        ChatRoom chatRoom = ChatRoom.builder()
                .id(roomId)
                .requesterId(userId1)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(members)
                .createdAt(LocalDateTime.now())
                .build();

        ChatRoom saved = chatRoomRepository.save(chatRoom);
        log.info("Created friendship chat room {} for {}/{}", roomId, userId1, userId2);
        return saved;
    }

    private UUID deterministicRoomId(UUID a, UUID b) {
        String[] ordered = a.compareTo(b) <= 0
                ? new String[]{a.toString(), b.toString()}
                : new String[]{b.toString(), a.toString()};
        String seed = "friendship:" + ordered[0] + ":" + ordered[1];
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}
