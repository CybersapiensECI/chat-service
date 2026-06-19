package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
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
        return chatRoomRepository.findAllByMemberId(userId).stream()
                .map(room -> chatMapper.toConnectionResponse(room, userId))
                .toList();
    }
}
