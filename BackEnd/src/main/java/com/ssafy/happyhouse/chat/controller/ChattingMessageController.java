package com.ssafy.happyhouse.chat.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.ssafy.happyhouse.chat.model.dto.ChattingMessageDto;
import com.ssafy.happyhouse.chat.model.service.ChattingService;
import com.ssafy.happyhouse.util.KafkaTopicProvider;
import com.ssafy.happyhouse.util.LocalTimeUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChattingMessageController {
    private final ChattingService service;
    private final SimpMessageSendingOperations messageTemplate;
    private final SimpUserRegistry userRegistry;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationContext applicationContext;

    @MessageMapping("/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") String roomId, @Payload ChattingMessageDto chatMessage) {
        String userId = chatMessage.getSender();
        if (chatMessage.getType().equals(ChattingMessageDto.MessageType.ENTER)) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장하셨습니다.");
            // 새로운 KafkaTopicProvider 인스턴스를 생성
            System.out.println("receiver = "+chatMessage.getReceiver());
            KafkaTopicProvider kafkaTopicProvider = applicationContext.getBean(KafkaTopicProvider.class, roomId, messageTemplate);
//            kafkaTemplate.send(roomId, new Gson().toJson(chatMessage));
            System.out.println("roomId = " + roomId + " // chatmessage = " + chatMessage);
            System.out.println("success to send");
        } else if (chatMessage.getType().equals(ChattingMessageDto.MessageType.QUIT)) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장하셨습니다.");
            messageTemplate.convertAndSend("/sub/" + roomId, chatMessage);
        } else if (chatMessage.getType().equals(ChattingMessageDto.MessageType.TALK)) {
            int result = service.insertMessage(chatMessage);
            // 새로운 KafkaTopicProvider 인스턴스를 생성
            KafkaTopicProvider kafkaTopicProvider = applicationContext.getBean(KafkaTopicProvider.class, roomId, messageTemplate);
            chatMessage.setMessageDate(LocalTimeUtil.getDateTime());
            System.out.println("연결 성공 및 메세지는 ="+chatMessage);
            kafkaTemplate.send(roomId, new Gson().toJson(chatMessage));
        }
    }
}