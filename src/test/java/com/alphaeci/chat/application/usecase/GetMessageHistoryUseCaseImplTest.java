package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import com.alphaeci.chat.domain.model.enums.MessageType;
import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import com.alphaeci.chat.domain.ports.out.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMessageHistoryUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ChatMapper chatMapper;

    @InjectMocks private GetMessageHistoryUseCaseImpl useCase;

    private UUID userId;
    private UUID chatRoomId;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId)))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_memberUser_returnsHistory() {
        PageRequest pageable = PageRequest.of(0, 10);
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content("Hola")
                .type(MessageType.TEXT)
                .sentAt(LocalDateTime.now())
                .build();

        MessageResponse response = MessageResponse.builder()
                .id(message.getId())
                .content("Hola")
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(messageRepository.findAllByChatRoomId(chatRoomId, pageable))
                .thenReturn(new PageImpl<>(List.of(message)));
        when(chatMapper.toMessageResponse(message)).thenReturn(response);

        Page<MessageResponse> result = useCase.execute(chatRoomId, userId, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void execute_chatRoomNotFound_throwsException() {
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        assertThrows(ChatRoomNotFoundException.class,
                () -> useCase.execute(chatRoomId, userId, PageRequest.of(0, 10)));
    }

    @Test
    void execute_userNotMember_throwsException() {
        UUID outsider = UUID.randomUUID();
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        assertThrows(UserNotMemberException.class,
                () -> useCase.execute(chatRoomId, outsider, PageRequest.of(0, 10)));
    }
}
