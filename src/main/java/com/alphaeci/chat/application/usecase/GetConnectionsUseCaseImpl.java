package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.in.GetConnectionsUseCase;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetConnectionsUseCaseImpl implements GetConnectionsUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<ConnectionResponse> execute(UUID userId) {
        // Solo salas 1:1 (parcheId nulo): las grupales de un parche se listan
        // aparte con GET /api/parches (memberCount y demás no tienen sentido
        // en un ConnectionResponse, que asume un único "otherUserId"). Solo
        // ACTIVE: el front no distingue status y pintaba solicitudes
        // PENDING o REJECTED como si fueran chats normales abiertos.
        return chatRoomRepository.findAllByMemberId(userId).stream()
                .filter(room -> room.getParcheId() == null)
                .filter(room -> room.getStatus() == ChatRoomStatus.ACTIVE)
                .map(room -> chatMapper.toConnectionResponse(room, userId))
                .toList();
    }
}
