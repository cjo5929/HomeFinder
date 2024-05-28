package com.ssafy.happyhouse.user.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.happyhouse.chat.model.dao.ChattingDao;
import com.ssafy.happyhouse.chat.model.service.ChattingService;
import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dao.UserDao;
import com.ssafy.happyhouse.user.model.dto.FavoriteDto;
import com.ssafy.happyhouse.user.model.dto.UserDto;
import com.ssafy.happyhouse.util.JWTUtil;
import com.ssafy.happyhouse.util.LocalTimeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserDao dao;
	private final ChattingDao chattingDao;
	private final JWTUtil jwtUtil;
	private final BCryptPasswordEncoder encoder;
	private final FavoriteService favoriteService;
	private final EstateService estateService;
	private final ChattingService chattingService;
	@Value("${file.upload-dir}")
    private String uploadDir;
	
	@Override
	public boolean findById(String id) {
		UserDto validation = dao.findById(id);
		if(validation == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public int regist(UserDto dto) {
		boolean flag = findById(dto.getUserId());
		if(flag) {
			dto.setModifiedDate(LocalTimeUtil.getDateTime());
			if(dto.getUserRole() == null || dto.getUserRole().equals(""))dto.setUserRole("user");
			dto.setUserPassword(encoder.encode(dto.getUserPassword()));
			return dao.regist(dto);
		}
		else {
			return 0;
		}
	}

	@Override
	public int update(UserDto dto) {
		dto.setModifiedDate(LocalTimeUtil.getDateTime());
		dto.setUserPassword(encoder.encode(dto.getUserPassword()));
		return dao.update(dto);
	}

	/**
	 * 유저 정보 삭제의 경우 연관되어있는 테이블도 함께 삭제해야 한다.
	 * 1. File_info 이미지 삭제
	 * 2. user의 Role이 user라면 찜목록(favorite_item) 조회 후 삭제
	 * 3. user의 Role이 estate라면 등록 매물(estate_item) 조회 후 삭제
	 * 4. 채팅 메시지 삭제 (receiver, sender로 존재하는 경우)
	 * 5. 채팅 방 삭제 ( 는 좀 더 고민 필요. 상대방에게 회원 탈퇴 메시지를 전송하고 상대가 삭제할 수 있게끔 하던가. )
	 */
	@Override
	@Transactional
	public int delete(String id) {
		System.out.println("delete File_ info call");
		deleteUserImage(id);
		deleteItem(id);
		return dao.delete(id);
	}

	@Override
	public UserDto findUserById(String id) {
		int result = dao.selectUserImageByCount(UserDto.builder().userId(id).build());
		UserDto user = dao.findById(id);
		user.setUserPassword("");
		System.out.println("user = "+user);
//		if(result == 1) {
//			user = dao.selectUserImage(id);
//		}
		System.out.println("user service = "+user);
		return user;
	}

	@Override
	public Map<String,Object> login(UserDto dto) {
		/*
		 * UserDto user = dao.findById(dto.getUserId());
		 * if(user.getUserPassword().equals(dto.getUserPassword())) { String accessToken
		 * = jwtUtil.createAccessToken(user.getUserId()); String refreshToken =
		 * jwtUtil.createRefreshToken(user.getUserId());
		 * user.setRefreshToken(refreshToken); dao.updateRefreshToken(user);
		 * log.debug("refreshToken : {}", refreshToken); log.debug("accessToken : {}",
		 * accessToken); Map<String, Object> map = new HashMap<>();
		 * map.put("refreshToken", refreshToken); map.put("accessToken", accessToken);
		 * return map; } else { return null;
		 * 
		 * }
		 */
		return null;
	}

	@Override
	public int updateRefreshToken(UserDto dto) {
		return dao.updateRefreshToken(dto);
	}

	@Override
	public int updateUserImage(UserDto dto) {
		return dao.updateUserImage(dto);
	}

	@Override
	public int insertUserImage(UserDto dto) {
		return dao.insertUserImage(dto);
	}

	@Override
	public int selectUserImageByCount(UserDto dto) {
		return dao.selectUserImageByCount(dto);
	}

	@Override
	public int changeImage(UserDto dto) {
		int result = selectUserImageByCount(dto);
		int changeReulst = 0;
		if(result == 0) {
			System.out.println("user = "+dto);
			changeReulst = insertUserImage(dto);
		}
		else {
			System.out.println("result = 1"+dto);
			changeReulst = updateUserImage(dto);
		}
		return changeReulst;
	}

	@Override
	public int deleteUserImage(String userId) {
		UserDto dto = dao.selectUserImage(userId);
		if(dto==null) {
			return 1;
		}
		String folder =dto.getUserImageDto().getSaveFolder();
		String fixedName = dto.getUserImageDto().getFixedName();
		Path filePath = Paths.get(uploadDir, folder, fixedName);
		try {
			Files.deleteIfExists(filePath);
			return dao.deleteUserImage(userId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public UserDto selectUserImage(String userId) {
		return dao.selectUserImage(userId);
	}
	
	public String encodeFileToBase64(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(fileBytes);
    }

	@Override
	public int removeRefreshToken(UserDto dto) {
		System.out.println("remove 토큰 실행. Parameter = "+dto);
		return dao.removeRefreshToken(dto.getUserId());
	}

	@Override
	public int resetPassword(UserDto dto) {
		dto.setUserPassword(encoder.encode(dto.getUserPassword()));
		return dao.resetPassword(dto);
	}
	
	
	/**
	 * 
	 * @param userId
	 * 
	 * Estate_item을 삭제하기 위해서는 estate_item에 연관된 애들을 먼저 삭제해야함.
	 * file_info 
	 * Chatting Room -> ChattingMessage 먼저 삭제
	 * Favorite Item
	 */
	@Transactional
	public void deleteItem(String userId) {
		UserDto user = findUserById(userId);
		System.out.println("delete Item call");
		if(user.getUserRole().equals("user")) {
			System.out.println("delete Favorite call");
			favoriteService.deleteAllFavoriteItem(userId);
		}
		else if(user.getUserRole().equals("estate")) {
			System.out.println("delete Favorite before delete estate");
			List<HouseInfo> estateItems = estateService.findAllEstateItemByUserId(userId);
			for(HouseInfo item : estateItems) {
				List<FavoriteDto> favoriteItems = favoriteService.selectAllFavoriteItemByItemNo(item.getItem_no());
				System.out.println("item = "+item);
				System.out.println("list = "+favoriteItems);
				System.out.println("favorite 아이템들 상태를 -1로 바꾸기");
				if(favoriteItems != null && favoriteItems.size()>0) {
					favoriteService.updateFavoriteItemsCauseDelete(favoriteItems);					
				}
			}
			System.out.println("싹다 없애기");
			estateService.deleteAllUserItem(userId);
		}
	}
	
}
