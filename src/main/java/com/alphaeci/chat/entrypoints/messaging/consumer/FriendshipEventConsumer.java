package com.alphaeci.chat.entrypoints.messaging.consumer;

import com.alphaeci.chat.application.usecase.CreateFriendshipRoomUseCaseImpl;
import com.alphaeci.chat.entrypoints.messaging.dto.FriendshipCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipEventConsumer {

    private final CreateFriendshipRoomUseCaseImpl createFriendshipRoomUseCase;

    @RabbitListener(queues = "${rabbitmq.queues.friendship}")
    public void consume(FriendshipCreatedEvent event) {
        createFriendshipRoomUseCase.execute(event.getUserId1(), event.getUserId2());
    }
}
