package com.alphaeci.chat.domain.exceptions;

import java.util.UUID;

public class UserNotMemberException extends RuntimeException {
    public UserNotMemberException(UUID userId, UUID chatRoomId) {
        super("User " + userId + " is not a member of chat room " + chatRoomId);
    }

    /** Para negar antes de que la sala exista (todavía no hay chatRoomId). */
    public UserNotMemberException(String message) {
        super(message);
    }
}
