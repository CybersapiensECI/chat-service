package com.alphaeci.chat.infrastructure.external.parche;

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
class ParcheServiceAdapterTest {

    @Mock private ParcheFeignClient parcheFeignClient;

    @InjectMocks private ParcheServiceAdapter adapter;

    @Test
    void isMember_userInMembersList_returnsTrue() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(parcheFeignClient.getMembers(parcheId)).thenReturn(List.of(
                new ParcheFeignClient.ParcheMemberDto(userId),
                new ParcheFeignClient.ParcheMemberDto(UUID.randomUUID())
        ));

        assertTrue(adapter.isMember(parcheId, userId));
    }

    @Test
    void isMember_userNotInMembersList_returnsFalse() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(parcheFeignClient.getMembers(parcheId)).thenReturn(List.of(
                new ParcheFeignClient.ParcheMemberDto(UUID.randomUUID())
        ));

        assertFalse(adapter.isMember(parcheId, userId));
    }

    @Test
    void isMember_parchesServiceUnreachable_failsClosed() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Request request = Request.create(Request.HttpMethod.GET, "/x", java.util.Map.of(), null,
                StandardCharsets.UTF_8, new RequestTemplate());
        when(parcheFeignClient.getMembers(parcheId))
                .thenThrow(new FeignException.ServiceUnavailable("down", request, null, null));

        assertFalse(adapter.isMember(parcheId, userId));
    }
}
