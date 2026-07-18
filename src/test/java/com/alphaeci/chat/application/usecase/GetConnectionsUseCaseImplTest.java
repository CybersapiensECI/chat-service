package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.mapper.ChatMapper;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetConnectionsUseCaseImplTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ChatMapper chatMapper;

    @InjectMocks private GetConnectionsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        lenient().when(messageRepository.findAllByChatRoomId(any(), any()))
                .thenReturn(Page.empty());
    }

    @Test
    void execute_returnsConnectionsForUser() {
        UUID userId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        ChatRoom room = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, otherId)))
                .createdAt(LocalDateTime.now())
                .build();

        ConnectionResponse response = ConnectionResponse.builder()
                .chatRoomId(room.getId())
                .otherUserId(otherId)
                .status(ChatRoomStatus.ACTIVE)
                .build();

        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(room));
        when(chatMapper.toConnectionResponse(room, userId)).thenReturn(response);

        List<ConnectionResponse> result = useCase.execute(userId);

        assertEquals(1, result.size());
        assertEquals(otherId, result.get(0).getOtherUserId());
    }

    @Test
    void execute_noConnections_returnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of());

        List<ConnectionResponse> result = useCase.execute(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void execute_excludesParcheGroupRooms() {
        UUID userId = UUID.randomUUID();
        ChatRoom directRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();
        ChatRoom parcheRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .parcheId(UUID.randomUUID())
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID(), UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(directRoom, parcheRoom));
        when(chatMapper.toConnectionResponse(directRoom, userId))
                .thenReturn(ConnectionResponse.builder().chatRoomId(directRoom.getId()).build());

        List<ConnectionResponse> result = useCase.execute(userId);

        assertEquals(1, result.size());
        assertEquals(directRoom.getId(), result.get(0).getChatRoomId());
        verify(chatMapper, never()).toConnectionResponse(eq(parcheRoom), any());
    }

    @Test
    void execute_excludesPendingAndRejectedRooms() {
        UUID userId = UUID.randomUUID();
        ChatRoom activeRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();
        ChatRoom pendingRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.PENDING)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();
        ChatRoom rejectedRoom = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.REJECTED)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRoomRepository.findAllByMemberId(userId))
                .thenReturn(List.of(activeRoom, pendingRoom, rejectedRoom));
        when(chatMapper.toConnectionResponse(activeRoom, userId))
                .thenReturn(ConnectionResponse.builder().chatRoomId(activeRoom.getId()).build());

        List<ConnectionResponse> result = useCase.execute(userId);

        assertEquals(1, result.size());
        assertEquals(activeRoom.getId(), result.get(0).getChatRoomId());
        verify(chatMapper, never()).toConnectionResponse(eq(pendingRoom), any());
        verify(chatMapper, never()).toConnectionResponse(eq(rejectedRoom), any());
    }

    @Test
    void execute_attachesLastMessagePreview() {
        UUID userId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();
        Message lastMessage = Message.builder()
                .id(UUID.randomUUID())
                .chatRoomId(room.getId())
                .content("Nos vemos a las 5")
                .sentAt(LocalDateTime.now())
                .deleted(false)
                .build();

        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(room));
        when(chatMapper.toConnectionResponse(room, userId))
                .thenReturn(ConnectionResponse.builder().chatRoomId(room.getId()).build());
        when(messageRepository.findAllByChatRoomId(eq(room.getId()), any()))
                .thenReturn(new PageImpl<>(List.of(lastMessage)));

        List<ConnectionResponse> result = useCase.execute(userId);

        assertEquals("Nos vemos a las 5", result.get(0).getLastMessageContent());
        assertEquals(lastMessage.getSentAt(), result.get(0).getLastMessageAt());
    }

    @Test
    void execute_lastMessageDeleted_showsPlaceholderInsteadOfContent() {
        UUID userId = UUID.randomUUID();
        ChatRoom room = ChatRoom.builder()
                .id(UUID.randomUUID())
                .requesterId(userId)
                .status(ChatRoomStatus.ACTIVE)
                .memberIds(new ArrayList<>(List.of(userId, UUID.randomUUID())))
                .createdAt(LocalDateTime.now())
                .build();
        Message deletedMessage = Message.builder()
                .id(UUID.randomUUID())
                .chatRoomId(room.getId())
                .content("contenido original")
                .sentAt(LocalDateTime.now())
                .deleted(true)
                .build();

        when(chatRoomRepository.findAllByMemberId(userId)).thenReturn(List.of(room));
        when(chatMapper.toConnectionResponse(room, userId))
                .thenReturn(ConnectionResponse.builder().chatRoomId(room.getId()).build());
        when(messageRepository.findAllByChatRoomId(eq(room.getId()), any()))
                .thenReturn(new PageImpl<>(List.of(deletedMessage)));

        List<ConnectionResponse> result = useCase.execute(userId);

        assertEquals("Mensaje eliminado", result.get(0).getLastMessageContent());
    }
}
