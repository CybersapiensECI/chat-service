package com.alphaeci.chat.infrastructure.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Mismo patrón que matching-service: las rutas /api/v1/internal/** de
 * profile-service exigen el header X-Internal-Key (InternalServiceGuard),
 * validado contra su propia env var INTERNAL_API_KEY. Deliberadamente sin
 * @Configuration — se aplica solo al cliente que la declare, para no
 * filtrar la key a clientes que no la necesitan.
 */
public class InternalFeignConfig {

    static final String INTERNAL_KEY_HEADER = "X-Internal-Key";

    @Value("${internal.api-key:}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor internalApiKeyInterceptor() {
        return template -> {
            if (internalApiKey != null && !internalApiKey.isBlank()) {
                template.header(INTERNAL_KEY_HEADER, internalApiKey);
            }
        };
    }
}
