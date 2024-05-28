package com.ssafy.happyhouse.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingMessageDto {
	public enum MessageType{
		ENTER, TALK, QUIT
	}
	private MessageType type;
	private String roomId;
	private String sender;
	private String receiver;
	private String message;
	private String messageDate;
}
