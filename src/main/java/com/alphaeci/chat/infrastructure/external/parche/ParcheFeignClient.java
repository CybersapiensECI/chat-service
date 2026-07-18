package com.alphaeci.chat.infrastructure.external.parche;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "parches-service",
        contextId = "parchesInternalChat",
        url = "${PARCHES_SERVICE_URL}"
)
public interface ParcheFeignClient {

    @GetMapping("/api/parches/{parcheId}/members")
    List<ParcheMemberDto> getMembers(@PathVariable UUID parcheId);

    record ParcheMemberDto(UUID studentId) {}
}
