package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.ports.in.GetConnectionsUseCase;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetConnectionsUseCaseImpl implements GetConnectionsUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
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
                .map(room -> withLastMessage(chatMapper.toConnectionResponse(room, userId), room.getId()))
                .toList();
    }

    /**
     * El front mostraba la biografía del contacto como subtítulo de cada
     * chat en vez del último mensaje — porque ConnectionResponse nunca lo
     * traía. Una consulta por sala (tamaño de página 1) es aceptable acá:
     * la lista de chats de un usuario no es grande y esto no es un hot path.
     */
    private ConnectionResponse withLastMessage(ConnectionResponse response, UUID chatRoomId) {
        List<Message> lastPage = messageRepository
                .findAllByChatRoomId(chatRoomId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "sentAt")))
                .getContent();
        if (lastPage.isEmpty()) {
            return response;
        }
        Message last = lastPage.get(0);
        return response.toBuilder()
                .lastMessageContent(last.isDeleted() ? "Mensaje eliminado" : last.getContent())
                .lastMessageAt(last.getSentAt())
                .build();
    }
}
