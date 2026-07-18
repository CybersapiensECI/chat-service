package com.alphaeci.chat.infrastructure.external.parche;

import com.alphaeci.chat.domain.ports.out.ParcheServicePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParcheServiceAdapter implements ParcheServicePort {

    private final ParcheFeignClient parcheFeignClient;

    @Override
    public boolean isMember(UUID parcheId, UUID userId) {
        try {
            return parcheFeignClient.getMembers(parcheId).stream()
                    .anyMatch(m -> userId.equals(m.studentId()));
        } catch (FeignException e) {
            log.warn("Parches-Service unavailable checking membership {}/{}: {}", parcheId, userId, e.getMessage());
            // Fallar cerrado: si no se puede verificar, no se une al chat.
            return false;
        }
    }
}
