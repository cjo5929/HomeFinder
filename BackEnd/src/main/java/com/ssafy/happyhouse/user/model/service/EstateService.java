package com.ssafy.happyhouse.user.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;

public interface EstateService {
	 int insertApart(HouseInfo dto, MultipartFile file);
	 Integer selectFileNo(HouseInfo dto);
	 List<HouseInfo> selectAllEstateItem(String userId);
	 List<HouseInfo> selectAllEstateItem(Integer aptCode);
	 List<UserImageDto> selectAllEstateImage(String userId);
	 Integer selectItemNo();
	 Integer findFileNoByItemNo(Integer itemNo);
	 int deleteApart(Integer itemNo);
	 int deleteApartImage(Integer fileNo);
	 
	 List<HouseInfo> findAllEstateItemByUserId(String userId);
	 
	 int deleteAllUserItem(String userId);
	 HouseInfo findHouseInfoByItemNo(Integer itemNo);
	 int updateEstateImage(MultipartFile file, Integer fileNo);
	 UserImageDto selectEstateImageByFileNo(Integer fileNo);
	 int updateEstateInfo(HouseInfo dto);
}
