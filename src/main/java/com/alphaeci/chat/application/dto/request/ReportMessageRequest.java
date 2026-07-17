package com.alphaeci.chat.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Moderation report against a message")
public class ReportMessageRequest {

    @NotNull
    @Schema(description = "Message being reported", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "5d4c3b2a-1f0e-49d8-8c7b-6a5f4e3d2c1b")
    private UUID messageId;

    @NotBlank
    @Size(max = 300)
    @Schema(description = "Why the message is being reported, at most 300 characters",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 300, example = "Contenido ofensivo")
    private String reason;
}
