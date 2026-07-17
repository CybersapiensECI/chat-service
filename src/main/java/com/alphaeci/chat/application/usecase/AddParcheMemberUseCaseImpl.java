package com.alphaeci.chat.application.usecase;

import com.alphaeci.chat.domain.ports.out.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddParcheMemberUseCaseImpl {

    private final ChatRoomRepository chatRoomRepository;

    public void execute(UUID parcheId, UUID studentId) {
        var room = chatRoomRepository.findById(parcheId).orElse(null);
        if (room == null) {
            log.warn("Parche room {} not found when adding member {} — parche.created event may have been "
                    + "missed or arrive later; nothing to add to yet", parcheId, studentId);
            return;
        }
        if (room.isMember(studentId)) {
            return;
        }
        room.addMember(studentId);
        chatRoomRepository.save(room);
        log.info("Added member {} to parche room {}", studentId, parcheId);
    }
}
