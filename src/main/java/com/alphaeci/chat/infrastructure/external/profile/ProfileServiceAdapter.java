package com.alphaeci.chat.infrastructure.external.profile;

import com.alphaeci.chat.domain.ports.out.ProfileServicePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceAdapter implements ProfileServicePort {

    private final ProfileFeignClient profileFeignClient;

    @Override
    public boolean areFriends(UUID userId, UUID otherUserId) {
        try {
            return profileFeignClient.getFriends(userId).contains(otherUserId);
        } catch (FeignException e) {
            log.warn("profile-service unavailable checking friendship {}/{}: {}", userId, otherUserId, e.getMessage());
            // Fallar cerrado: si no se puede verificar, no se abre el chat.
            return false;
        }
    }
}
