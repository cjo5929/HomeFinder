package com.ssafy.happyhouse.chat.model.dao;

import java.util.List;

import com.ssafy.happyhouse.chat.model.dto.ChattingMessageDto;
import com.ssafy.happyhouse.chat.model.dto.ChattingRoomDto;

public interface ChattingDao {
	List<ChattingRoomDto> selectAllRoom();
	
	int insert(ChattingRoomDto dto);
	
	ChattingRoomDto findRoomById(String roomId);
	
	int insertMessage(ChattingMessageDto message);
	
	List<ChattingMessageDto> bringPrevMessage(String roomId);
	
	ChattingRoomDto selectRoomBeforeCreate(ChattingRoomDto dto);
	
	String selectRoomIdBySRI(ChattingRoomDto dto);
	List<ChattingMessageDto> findAllPrevChatMessage(String roomId);
	
	List<ChattingRoomDto> findAllRoomByUserId(String userId);
	
	List<ChattingRoomDto> findAllRoomByUserIdSender(String userId);
	
	List<ChattingRoomDto> findAllRoomByUserIdReceiverOrSender(String userId);
	
	String findUserRole(String userId);
	
	int updateRoomCauseDeleteFromSender(String roomId);
	int updateRoomCauseDeleteFromReceiver(String roomId);
	int deleteRoom(String roomId);
	
	String selectEstateNameByItemNo(Integer itemNo);
}
