package com.ssafy.happyhouse.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.happyhouse.chat.model.dto.ChattingMessageDto;
import com.ssafy.happyhouse.chat.model.dto.ChattingRoomDto;
import com.ssafy.happyhouse.chat.model.service.ChattingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChattingController {
	private final ChattingService service;
	
	@GetMapping("/list/{userId}")
	public ResponseEntity<?> room(@PathVariable("userId")String userId){
		List<ChattingRoomDto> list = service.selectAllRoom();
		System.out.println("all room select");
		System.out.println("list = "+list);
		return null;
	}
	@PostMapping("/createRoom")
	public ResponseEntity<?> createRoom(@RequestBody ChattingRoomDto data) {
		System.out.println("receive data = "+data);
		int result = service.createRoom(data);
		System.out.println("result = "+result);
		return null;
//		ChattingRoomDto dto = service.createRoom(roomName);
//		int result = service.insert(dto);
//		System.out.println("createRoom and result = "+result);
//		return null;
	}
	@GetMapping("/room/{chatRoomId}")
	public ResponseEntity<?> moveRoom(@PathVariable("chatRoomId")String roomId){
		ChattingRoomDto room = service.findRoomById(roomId);
		System.out.println("find room by id = "+room);
		return null;
	}
	
	@GetMapping("/room")
	public ResponseEntity<?> findRoomIdBySenderAndReceiver(@RequestParam Map<String, String> params){
		String sender = params.get("sender");
		String receiver = params.get("receiver");
		Integer itemNo = Integer.valueOf(params.get("itemNo"));
		ChattingRoomDto room = ChattingRoomDto.builder()
								.sender(sender)
								.receiver(receiver)
								.itemNo(itemNo).build();
		String roomId = service.selectRoomIdBySRI(room);
		Map<String, Object> map = new HashMap<>();
		map.put("roomId", roomId);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	@GetMapping("/{roomId}")
	public ResponseEntity<?> findAllPrevChatMessage(@PathVariable("roomId") String roomId){
		System.out.println("으악 으악 = "+roomId);
		List<ChattingMessageDto> list = service.findAllPrevChatMessage(roomId);
		if(list == null || list.size() == 0) {
			list = new ArrayList<>();
		}
		Map<String, Object> map = new HashMap<>();
		System.out.println("list = "+list);
		map.put("list", list);
		ChattingRoomDto chatRoom = service.findRoomById(roomId);
		if(chatRoom.getSenderDelete() == 1 || chatRoom.getReceiverDelete()==1) {
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);			
		}
	}
	
	@GetMapping("/rooms/{userId}")
	public ResponseEntity<?> findAllRoomByUserId(@PathVariable("userId") String userId){
		List<ChattingRoomDto> list = service.findAllRoomByUserId(userId);
		if(list==null || list.size()==0) {
			list = new ArrayList<>();
		}
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
	}
	
	@DeleteMapping("/room/{userId}")
	public ResponseEntity<?> deleteRoom(@PathVariable("userId")String userId, @RequestBody ChattingRoomDto dto){
		System.out.println("delete room userId = "+userId);
		System.out.println("delete room = "+dto);
		service.deleteChatRoom(userId, dto);
		
		return ResponseEntity.ok("ok");
	}
	
}
