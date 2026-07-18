package com.alphaeci.chat.infrastructure.adapters.adapter;

import com.alphaeci.chat.domain.model.Message;
import com.alphaeci.chat.domain.ports.out.ChatEventPublisher;
import com.alphaeci.chat.entrypoints.messaging.publisher.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatEventPublisherAdapter implements ChatEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.chat}")
    private String chatRoutingKey;

    @Override
    public void publishMessageSent(Message message, String senderId) {
        ChatMessageEvent event = ChatMessageEvent.builder()
                .chatId(message.getChatRoomId())
                .senderId(senderId)
                .content(message.getContent())
                .build();

        rabbitTemplate.convertAndSend(exchange, chatRoutingKey, event);
    }
}
