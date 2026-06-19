package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMessageHistoryUseCaseImpl implements GetMessageHistoryUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;

    @Override
    public Page<MessageResponse> execute(UUID chatRoomId, UUID userId, Pageable pageable) {
        var chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId));

        if (!chatRoom.isMember(userId)) {
            throw new UserNotMemberException(userId, chatRoomId);
        }

        return messageRepository.findAllByChatRoomId(chatRoomId, pageable)
                .map(chatMapper::toMessageResponse);
    }
}
