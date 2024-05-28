package com.ssafy.happyhouse.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.service.EstateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/estate")
public class EstateController {
	private final EstateService service;
	
	@PostMapping("/item")
	public ResponseEntity<?> insertApart(@RequestParam(value="image", required = false)MultipartFile file,
			@RequestParam("dto") String dtoJson){
		if(file == null) {
			System.out.println("file is null");
		}
		System.out.println("item 도착");
		ObjectMapper objectMapper = new ObjectMapper();
	    HouseInfo dto = null;
		try {
			dto = objectMapper.readValue(dtoJson, HouseInfo.class);
			service.insertApart(dto, file);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("매물 저장 및 이미지 등록 성공");
		return null;
	}
	
	@GetMapping("/item/{userId}")
	public ResponseEntity<?> selectAllEstateItem(@PathVariable("userId")String userId){
		List<HouseInfo> itemList = service.selectAllEstateItem(userId);
		
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> map = new HashMap<>();
		map.put("list", itemList);
		return new ResponseEntity<Map<String,Object>>(map, status);
	}
	
	@DeleteMapping("/item")
	public ResponseEntity<?> deleteItem(@RequestParam("item_no")Integer itemNo){
		System.out.println("data 전달 완료. item no = "+itemNo);
		HttpStatus status = HttpStatus.ACCEPTED;
		int result = service.deleteApart(itemNo);
		if(result == 1)status = HttpStatus.OK;
		else status = HttpStatus.INTERNAL_SERVER_ERROR;
		return new ResponseEntity<Void>(status);
	}
	
	@PutMapping("/item/{itemNo}")
	public ResponseEntity<?> updateItem(@PathVariable("itemNo")Integer itemNo,
			@RequestParam(value="image", required = false) MultipartFile file,
			@RequestParam("file_no")Integer fileNo,
			@RequestParam("price")String price,
			@RequestParam("address")String address,
			@RequestParam("floor")Integer floor,
			@RequestParam("info")String info,
			@RequestParam("move_in")String move_in,
			@RequestParam("room")Integer room,
			@RequestParam("type")String type,
			@RequestParam("heater")String heater,
			@RequestParam("bath")Integer bath,
			@RequestParam("entrance")String entrance,
			@RequestParam("area")Double area){
		System.out.println("item no ="+itemNo);
		System.out.println("file no = "+fileNo);
		System.out.println("price = "+price);
		System.out.println("address = "+address);
		System.out.println("floor = "+floor);
		System.out.println("info = "+info);
		System.out.println("move_in = "+move_in);
		System.out.println("room = "+room);
		System.out.println("type = "+type);
		System.out.println("heater = "+heater);
		System.out.println("bath = "+bath);
		System.out.println("entrance = "+entrance);
		HouseInfo sendObject = HouseInfo.builder()
								.file_no(fileNo)
								.price(price)
								.address(address)
								.floor(floor)
								.info(info)
								.move_in(move_in)
								.room(room)
								.type(type)
								.heater(heater)
								.bath(bath)
								.entrance(entrance).item_no(itemNo).area(area).build();
		if(file != null) {
			service.updateEstateImage(file, fileNo);
		}
		System.out.println("send Object = "+sendObject);
		service.updateEstateInfo(sendObject);
		return ResponseEntity.ok("ok");
	}
	
	
}
