package com.ssafy.happyhouse.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingRoomDto {
	private String roomId;
	private String name;
	private String sender;
	private String receiver;
	private Integer itemNo;
	private Integer senderDelete;
	private Integer receiverDelete;
	
	private String estateName;
}

