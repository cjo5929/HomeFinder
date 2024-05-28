package com.ssafy.happyhouse.chat.model.service;

import java.util.List;

import com.ssafy.happyhouse.chat.model.dto.ChattingMessageDto;
import com.ssafy.happyhouse.chat.model.dto.ChattingRoomDto;

public interface ChattingService {
	
	List<ChattingRoomDto> selectAllRoom();
		
	int insert(ChattingRoomDto dto);
	
	ChattingRoomDto findRoomById(String roomId);
	
	int insertMessage(ChattingMessageDto message);
	
	List<ChattingMessageDto> bringPrevMessage(String roomId);
	
	int createRoom(ChattingRoomDto dto);
	ChattingRoomDto selectRoomBeforeCreate(ChattingRoomDto dto);
	String selectRoomIdBySRI(ChattingRoomDto dto);
	List<ChattingMessageDto> findAllPrevChatMessage(String roomId);
	List<ChattingRoomDto> findAllRoomByUserId(String userId);
	List<ChattingRoomDto> findAllRoomByUserIdSender(String userId);
	
	List<ChattingRoomDto> findAllRoomByUserIdReceiverOrSender(String userId);
	
	String findUserRole(String userId);
	int updateRoomCauseDeleteFromSender(String roomId);
	int updateRoomCauseDeleteFromReceiver(String roomId);
	
	void deleteChatRoom(String userId, ChattingRoomDto dto);
	int deleteRoom(String roomId);
	
	String selectEstateNameByItemNo(Integer itemNo);
}
