package com.alphaeci.chat.application.mapper;

import com.alphaeci.chat.application.dto.response.ConnectionResponse;
import com.alphaeci.chat.application.dto.response.MessageReportResponse;
import com.alphaeci.chat.application.dto.response.MessageResponse;
import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.MessageReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    MessageResponse toMessageResponse(Message message);

    MessageReportResponse toMessageReportResponse(MessageReport report);

    @Mapping(target = "chatRoomId", source = "chatRoom.id")
    @Mapping(target = "otherUserId", expression = "java(resolveOtherUserId(chatRoom, currentUserId))")
    @Mapping(target = "status", source = "chatRoom.status")
    @Mapping(target = "createdAt", source = "chatRoom.createdAt")
    ConnectionResponse toConnectionResponse(ChatRoom chatRoom, UUID currentUserId);

    default UUID resolveOtherUserId(ChatRoom chatRoom, UUID currentUserId) {
        return chatRoom.getMemberIds().stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .orElse(null);
    }
}
