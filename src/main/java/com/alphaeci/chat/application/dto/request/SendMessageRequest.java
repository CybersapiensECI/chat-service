package com.alphaeci.chat.application.dto.request;

import com.alphaeci.chat.domain.model.enums.MessageType;
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
public class SendMessageRequest {
    @NotNull
    private MessageType type;
    @Size(max = 500)
    private String content;
    private String mediaUrl;
}
