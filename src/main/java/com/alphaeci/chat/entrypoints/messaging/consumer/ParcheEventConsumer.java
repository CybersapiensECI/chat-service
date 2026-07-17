package com.alphaeci.chat.entrypoints.messaging.consumer;

import com.alphaeci.chat.application.usecase.AddParcheMemberUseCaseImpl;
import com.alphaeci.chat.application.usecase.CreateParcheRoomUseCaseImpl;
import com.alphaeci.chat.entrypoints.messaging.dto.ParcheCreatedEvent;
import com.alphaeci.chat.entrypoints.messaging.dto.ParcheMemberJoinedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParcheEventConsumer {

    private final CreateParcheRoomUseCaseImpl createParcheRoomUseCase;
    private final AddParcheMemberUseCaseImpl addParcheMemberUseCase;

    @RabbitListener(queues = "${rabbitmq.queues.parche-created}")
    public void consumeCreated(ParcheCreatedEvent event) {
        createParcheRoomUseCase.execute(event.getParcheId(), event.getCreatorId());
    }

    @RabbitListener(queues = "${rabbitmq.queues.parche-member-joined}")
    public void consumeMemberJoined(ParcheMemberJoinedEvent event) {
        addParcheMemberUseCase.execute(event.getParcheId(), event.getStudentId());
    }
}
