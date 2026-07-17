package com.alphaeci.chat.application.dto.request;

import com.alphaeci.chat.domain.model.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = """
        Message to publish in a chat room. Sent over STOMP to /app/chat/{chatRoomId}/send, not through
        the REST API. A TEXT message requires content; an IMAGE message requires mediaUrl.
        """)
public class SendMessageRequest {

    @NotNull
    @Schema(description = "Message kind", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEXT")
    private MessageType type;

    @Size(max = 500)
    @Schema(description = "Text body, at most 500 characters. Required when type is TEXT.",
            maxLength = 500, example = "¿A qué hora nos vemos?")
    private String content;

    @Schema(description = "Media URL. Required when type is IMAGE.",
            example = "https://cdn.patricia.app/media/parche-42.jpg")
    private String mediaUrl;
}
