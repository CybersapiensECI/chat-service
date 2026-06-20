package com.alphaeci.chat.infrastructure.adapters.persistence;

import com.alphaeci.chat.domain.model.ChatRoom;
import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.model.MessageReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PersistenceMapper {

    @Mapping(target = "id", expression = "java(doc.getId() != null ? java.util.UUID.fromString(doc.getId()) : null)")
    @Mapping(target = "parcheId", expression = "java(doc.getParcheId() != null ? java.util.UUID.fromString(doc.getParcheId()) : null)")
    @Mapping(target = "requesterId", expression = "java(doc.getRequesterId() != null ? java.util.UUID.fromString(doc.getRequesterId()) : null)")
    @Mapping(target = "status", expression = "java(com.alphaeci.chat.domain.model.enums.ChatRoomStatus.valueOf(doc.getStatus()))")
    @Mapping(target = "memberIds", expression = "java(toUUIDList(doc.getMemberIds()))")
    ChatRoom toChatRoom(ChatRoomDocument doc);

    @Mapping(target = "id", expression = "java(room.getId() != null ? room.getId().toString() : null)")
    @Mapping(target = "parcheId", expression = "java(room.getParcheId() != null ? room.getParcheId().toString() : null)")
    @Mapping(target = "requesterId", expression = "java(room.getRequesterId() != null ? room.getRequesterId().toString() : null)")
    @Mapping(target = "status", expression = "java(room.getStatus().name())")
    @Mapping(target = "memberIds", expression = "java(toStringList(room.getMemberIds()))")
    ChatRoomDocument toChatRoomDocument(ChatRoom room);

    @Mapping(target = "id", expression = "java(doc.getId() != null ? java.util.UUID.fromString(doc.getId()) : null)")
    @Mapping(target = "chatRoomId", expression = "java(doc.getChatRoomId() != null ? java.util.UUID.fromString(doc.getChatRoomId()) : null)")
    @Mapping(target = "senderId", expression = "java(doc.getSenderId() != null ? java.util.UUID.fromString(doc.getSenderId()) : null)")
    @Mapping(target = "type", expression = "java(com.alphaeci.chat.domain.model.enums.MessageType.valueOf(doc.getType()))")
    Message toMessage(MessageDocument doc);

    @Mapping(target = "id", expression = "java(msg.getId() != null ? msg.getId().toString() : null)")
    @Mapping(target = "chatRoomId", expression = "java(msg.getChatRoomId() != null ? msg.getChatRoomId().toString() : null)")
    @Mapping(target = "senderId", expression = "java(msg.getSenderId() != null ? msg.getSenderId().toString() : null)")
    @Mapping(target = "type", expression = "java(msg.getType().name())")
    MessageDocument toMessageDocument(Message msg);

    @Mapping(target = "id", expression = "java(doc.getId() != null ? java.util.UUID.fromString(doc.getId()) : null)")
    @Mapping(target = "messageId", expression = "java(doc.getMessageId() != null ? java.util.UUID.fromString(doc.getMessageId()) : null)")
    @Mapping(target = "reporterId", expression = "java(doc.getReporterId() != null ? java.util.UUID.fromString(doc.getReporterId()) : null)")
    @Mapping(target = "status", expression = "java(com.alphaeci.chat.domain.model.enums.ReportStatus.valueOf(doc.getStatus()))")
    MessageReport toMessageReport(MessageReportDocument doc);

    @Mapping(target = "id", expression = "java(report.getId() != null ? report.getId().toString() : null)")
    @Mapping(target = "messageId", expression = "java(report.getMessageId() != null ? report.getMessageId().toString() : null)")
    @Mapping(target = "reporterId", expression = "java(report.getReporterId() != null ? report.getReporterId().toString() : null)")
    @Mapping(target = "status", expression = "java(report.getStatus().name())")
    MessageReportDocument toMessageReportDocument(MessageReport report);

    default List<UUID> toUUIDList(List<String> ids) {
        if (ids == null) return List.of();
        return ids.stream().map(UUID::fromString).collect(Collectors.toList());
    }

    default List<String> toStringList(List<UUID> ids) {
        if (ids == null) return List.of();
        return ids.stream().map(UUID::toString).collect(Collectors.toList());
    }
}
