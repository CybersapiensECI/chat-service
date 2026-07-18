package com.alphaeci.chat.infrastructure.external.profile;

import com.alphaeci.chat.infrastructure.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "profile-service",
        contextId = "profileInternalChat",
        url = "${PROFILE_SERVICE_URL}",
        path = "/api/v1/internal",
        configuration = InternalFeignConfig.class
)
public interface ProfileFeignClient {

    @GetMapping("/users/{userId}/friends")
    List<UUID> getFriends(@PathVariable UUID userId);
}
