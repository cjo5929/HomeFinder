package com.ssafy.happyhouse.user.model.dao;

import java.util.List;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dto.FavoriteDto;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;

public interface FavoriteDao {
	int addFavorite(FavoriteDto dto);
	List<HouseInfo> findAllUserFavorite(String userId);
	UserImageDto findApartImage(Integer file_no);
	FavoriteDto findFavoriteByItemAndUserId(FavoriteDto dto);
	int deleteFavorite(FavoriteDto dto);
	int deleteAllFavoriteItem(String userId);
	
	
	List<FavoriteDto> selectAllFavoriteItemByItemNo(Integer itemNo);
	
	int updateFavoriteItemsCauseDelete(List<FavoriteDto> list);
	
	List<FavoriteDto> findAllUserFavoriteByUserId(String userId);
}
