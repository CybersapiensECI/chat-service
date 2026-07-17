package com.alphaeci.chat.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chat Service API")
                        .description("""
                                AlphaECI — PATRICI.A Chat Microservice.

                                Real-time messaging inside a parche. This REST API covers message history,
                                moderation reports and the connection lifecycle between users
                                (PENDING -> ACTIVE / REJECTED).

                                Sending a message is NOT part of this API: publish over STOMP to
                                `/app/chat/{chatRoomId}/send` (connect at `/ws-chat`, SockJS) and subscribe to
                                `/topic/parche/{chatRoomId}/messages` to receive them. Each message sent also
                                publishes a `chat.message` event on the `notification.exchange` for the
                                Notification Service.

                                Authentication is handled by the API Gateway, which propagates the
                                authenticated user in the `X-User-Id` header.
                                """)
                        .version("1.0.0")
                        .license(new License().name("Escuela Colombiana de Ingeniería Julio Garavito")))
                .tags(List.of(
                        new Tag().name("Chat — Messages").description("Message history and content moderation"),
                        new Tag().name("Chat — Connections").description("Connection requests between users and their chat rooms")))
                .components(new Components()
                        .addParameters("XUserId", new HeaderParameter()
                                .name("X-User-Id")
                                .description("Authenticated user, propagated by the API Gateway")
                                .required(true)
                                .schema(new StringSchema().format("uuid"))));
    }
}
