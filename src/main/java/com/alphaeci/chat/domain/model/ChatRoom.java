package com.alphaeci.chat.domain.model;

import com.alphaeci.chat.domain.model.enums.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private UUID id;
    private UUID parcheId;
    private UUID requesterId;
    private ChatRoomStatus status;
    @Builder.Default
    private List<UUID> memberIds = new ArrayList<>();
    private LocalDateTime createdAt;

    public boolean isMember(UUID userId) {
        return memberIds.contains(userId);
    }

    public void addMember(UUID userId) {
        if (!isMember(userId)) {
            memberIds.add(userId);
        }
    }

    public void removeMember(UUID userId) {
        memberIds.remove(userId);
    }
}
