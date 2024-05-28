package com.ssafy.happyhouse.user.model.dao;

import java.util.List;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dto.UserDto;

public interface UserDao {
	int regist(UserDto dto);
	int update(UserDto dto);
	int delete(String id);
	UserDto findById(String id);
	int updateRefreshToken(UserDto dto);
	int removeRefreshToken(String userId);
	
	int updateUserImage(UserDto dto);
	int insertUserImage(UserDto dto);
	int selectUserImageByCount(UserDto dto);
	UserDto selectUserImage(String userId);
	int deleteUserImage(String userId);
	int resetPassword(UserDto dto);
}
