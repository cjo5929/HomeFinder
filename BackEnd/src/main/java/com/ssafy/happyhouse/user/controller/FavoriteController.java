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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dto.FavoriteDto;
import com.ssafy.happyhouse.user.model.service.FavoriteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/favorite")
@RestController
public class FavoriteController {
	private final FavoriteService favoriteService;

	@PostMapping("/{userId}")
	public ResponseEntity<?> addFavorite(@RequestBody HouseInfo dto, @PathVariable("userId")String userId){
		FavoriteDto item = FavoriteDto.builder()
							.userId(userId)
							.itemNo(dto.getItem_no())
							.itemType(dto.getType())
							.build();
		System.out.println("userId = "+userId);
		System.out.println("item = "+item);
		System.out.println("dto = "+dto);
		int result = favoriteService.addFavorite(item);
		if(result == 1) {
			return ResponseEntity.ok("ok");
		}
		else {
			HttpStatus status = HttpStatus.ACCEPTED;
			Map<String, Object> map = new HashMap<>();
			map.put("message", "이미 존재하는 찜입니다.");
			return new ResponseEntity<Map<String,Object>>(map,status);
		}
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<?> findAllUserFavorite(@PathVariable("userId") String userId){
		log.debug("user id = {}", userId);
		List<HouseInfo> favoriteList = favoriteService.findAllUserFavorite(userId);
		System.out.println(favoriteList);
		Map<String, Object> map = new HashMap<>();
		map.put("list", favoriteList);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteFavorite(@PathVariable("userId")String userId, @RequestParam("favoriteNo") Integer favoriteNo){
		System.out.println("user Id = "+userId + " /// itemNo = "+favoriteNo);
		FavoriteDto dto = FavoriteDto.builder().userId(userId).favoriteNo(favoriteNo).build();
		int result= favoriteService.deleteFavorite(dto);
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if(result == 1) {
			status = HttpStatus.OK;
		}
		return new ResponseEntity<Map<String,Object>>(map, status);
		
	}
}
