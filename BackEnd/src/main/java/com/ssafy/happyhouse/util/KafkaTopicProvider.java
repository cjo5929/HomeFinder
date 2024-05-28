package com.ssafy.happyhouse.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class KafkaTopicProvider {

    private final String chatRoomId;
    private final SimpMessageSendingOperations messageTemplate;
//    private final String receiver;
    
    public KafkaTopicProvider(@Value("#{chatRoomId}") String chatRoomId,
    		SimpMessageSendingOperations messageTemplate) {
        this.chatRoomId = chatRoomId;
        this.messageTemplate = messageTemplate;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    @KafkaListener(topics = "#{__listener.chatRoomId}", groupId = "chat-group")
    public void listenToRoom(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String chatRoomId) {
        System.out.println("Received message for room " + chatRoomId + ": " + message);
//        messageTemplate.convertAndSend("/user/"+receiver+"/sub/"+chatRoomId, message);
        
        messageTemplate.convertAndSend("/sub/"+chatRoomId, message);
    }
}