package com.ssafy.happyhouse.user.model.dao;

import java.util.List;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;

public interface EstateDao {
	int insertApart(HouseInfo dto);
	int insertApartImage(HouseInfo dto);
	Integer selectFileNo(HouseInfo dto);
	List<HouseInfo> selectAllEstateItem(String userId);
	List<UserImageDto> selectAllEstateImage(String userId);
	List<HouseInfo> selectAllEstateItemByAptCode(Integer aptCode);
	Integer selectItemNo();
	Integer findFileNoByItemNo(Integer itemNo);
	int deleteApart(Integer itemNo);
	int deleteApartImage(Integer fileNo);
	UserImageDto selectFileInfoByFileNo(Integer file_no);
	
	List<HouseInfo> findAllEstateItemByUserId(String userId);
	int deleteAllUserItem(String userId);
	
	HouseInfo findHouseInfoByItemNo(Integer itemNo);
	
	int updateEstateImage(UserImageDto dto);
	UserImageDto selectEstateImageByFileNo(Integer fileNo);
	int updateEstateInfo(HouseInfo dto);
}
