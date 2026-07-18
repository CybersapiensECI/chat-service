package com.alphaeci.chat.infrastructure.external.profile;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceAdapterTest {

    @Mock private ProfileFeignClient profileFeignClient;

    @InjectMocks private ProfileServiceAdapter adapter;

    @Test
    void areFriends_otherUserInFriendsList_returnsTrue() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        when(profileFeignClient.getFriends(userId)).thenReturn(List.of(friendId, UUID.randomUUID()));

        assertTrue(adapter.areFriends(userId, friendId));
    }

    @Test
    void areFriends_otherUserNotInFriendsList_returnsFalse() {
        UUID userId = UUID.randomUUID();
        UUID strangerId = UUID.randomUUID();
        when(profileFeignClient.getFriends(userId)).thenReturn(List.of(UUID.randomUUID()));

        assertFalse(adapter.areFriends(userId, strangerId));
    }

    @Test
    void areFriends_profileServiceUnreachable_failsClosed() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        Request request = Request.create(Request.HttpMethod.GET, "/x", java.util.Map.of(), null,
                StandardCharsets.UTF_8, new RequestTemplate());
        when(profileFeignClient.getFriends(userId))
                .thenThrow(new FeignException.ServiceUnavailable("down", request, null, null));

        assertFalse(adapter.areFriends(userId, friendId));
    }
}
