package com.ssafy.happyhouse.user.model.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.ssafy.happyhouse.user.model.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
	int regist(UserDto dto);
	int update(UserDto dto);
	int delete(String id);
	boolean findById(String id);
	UserDto findUserById(String id);
	Map<String,Object> login(UserDto dto);
	int updateRefreshToken(UserDto dto);
	int removeRefreshToken(UserDto dto);
	int updateUserImage(UserDto dto);
	int insertUserImage(UserDto dto);
	int selectUserImageByCount(UserDto dto);
	int changeImage(UserDto dto);
	int deleteUserImage(String userId);
	UserDto selectUserImage(String userId);
	String encodeFileToBase64(File file)throws IOException;
	int resetPassword(UserDto dto);
}

