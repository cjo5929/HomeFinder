package com.ssafy.happyhouse.chat.model.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happyhouse.chat.model.dao.ChattingDao;
import com.ssafy.happyhouse.chat.model.dto.ChattingMessageDto;
import com.ssafy.happyhouse.chat.model.dto.ChattingRoomDto;
import com.ssafy.happyhouse.user.model.dao.EstateDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService{
	private final ChattingDao dao;
	
	private final ObjectMapper objectMapper;

	public ChattingRoomDto findRoomById(String roomId) {
		return dao.findRoomById(roomId);
	}
	
	public int createRoom(ChattingRoomDto dto) {
		ChattingRoomDto vali = dao.selectRoomBeforeCreate(dto);
		if(vali==null) {
			String randomId = UUID.randomUUID().toString();
			ChattingRoomDto chatRoom = ChattingRoomDto.builder()
										.roomId(randomId)
										.name(dto.getSender()+" to "+dto.getReceiver())
										.sender(dto.getSender())
										.receiver(dto.getReceiver())
										.itemNo(dto.getItemNo())
										.build();
			return dao.insert(chatRoom);
		}
		return 0;
	}
	@Override
	public ChattingRoomDto selectRoomBeforeCreate(ChattingRoomDto dto) {
		return dao.selectRoomBeforeCreate(dto);
	}
	
	public List<ChattingRoomDto> selectAllRoom(){
		return dao.selectAllRoom();
	}
	public int insert(ChattingRoomDto room) {
		return dao.insert(room);
	}
	
	public int insertMessage(ChattingMessageDto message) {
		message.setMessageDate(returnNow());
		return dao.insertMessage(message);
	}
	
	public String returnNow() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
		String formatDate = now.format(format);
		return formatDate;
	}
	
	public List<ChattingMessageDto> bringPrevMessage(String roomId){
		List<ChattingMessageDto> list = dao.bringPrevMessage(roomId);
		for(ChattingMessageDto message : list) {
			message.setType(ChattingMessageDto.MessageType.ENTER);
		}
		return list;
	}

	@Override
	public String selectRoomIdBySRI(ChattingRoomDto dto) {
		return dao.selectRoomIdBySRI(dto);
	}

	@Override
	public List<ChattingMessageDto> findAllPrevChatMessage(String roomId) {
		return dao.findAllPrevChatMessage(roomId);
	}

	@Override
	public List<ChattingRoomDto> findAllRoomByUserId(String userId) {
		String userRole = findUserRole(userId);
		if(userRole.equals("estate")) {
			List<ChattingRoomDto> roomList = dao.findAllRoomByUserId(userId);
			for(ChattingRoomDto roomitem : roomList) {
				String estateName = dao.selectEstateNameByItemNo(roomitem.getItemNo());
				roomitem.setEstateName(estateName);
			}
			return roomList;
		}
		else if(userRole.equals("user")) {
			List<ChattingRoomDto> roomList = dao.findAllRoomByUserId(userId);
			for(ChattingRoomDto roomitem : roomList) {
				String estateName = dao.selectEstateNameByItemNo(roomitem.getItemNo());
				roomitem.setEstateName(estateName);
			}
			return roomList;
		}
		else {
			return null;
		}
	}

	@Override
	public List<ChattingRoomDto> findAllRoomByUserIdReceiverOrSender(String userId) {
		return dao.findAllRoomByUserIdReceiverOrSender(userId);
	}

	@Override
	public String findUserRole(String userId) {
		return dao.findUserRole(userId);
	}

	@Override
	public int updateRoomCauseDeleteFromSender(String roomId) {
		return dao.updateRoomCauseDeleteFromSender(roomId);
	}

	@Override
	public int updateRoomCauseDeleteFromReceiver(String roomId) {
		return dao.updateRoomCauseDeleteFromReceiver(roomId);
	}

	@Override
	public void deleteChatRoom(String userId, ChattingRoomDto dto) {
		String userRole = findUserRole(userId);
		if(userRole.equals("user")) {
			updateRoomCauseDeleteFromSender(dto.getRoomId());
		}
		else if(userRole.equals("estate")) {
			updateRoomCauseDeleteFromReceiver(dto.getRoomId());
		}
		ChattingRoomDto roomStatus = findRoomById(dto.getRoomId());
		System.out.println("roomStatus = "+roomStatus);
		if(roomStatus.getReceiverDelete() == 1 && roomStatus.getReceiverDelete() == 1) {
			deleteRoom(roomStatus.getRoomId());
		}
	}

	@Override
	public List<ChattingRoomDto> findAllRoomByUserIdSender(String userId) {
		return dao.findAllRoomByUserIdSender(userId);
	}

	@Override
	public int deleteRoom(String roomId) {
		return dao.deleteRoom(roomId);
	}

	@Override
	public String selectEstateNameByItemNo(Integer itemNo) {
		return dao.selectEstateNameByItemNo(itemNo);
	}

}
