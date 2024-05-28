package com.ssafy.happyhouse.user.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dao.FavoriteDao;
import com.ssafy.happyhouse.user.model.dto.FavoriteDto;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService{
	private final FavoriteDao dao;
	private final EstateService estateService;
	@Override
	public int addFavorite(FavoriteDto dto) {
		Integer item_no = dto.getItemNo();
		FavoriteDto vali = findFavoriteByItemAndUserId(dto);
		int result = 0;
		if(vali == null) {
			result = dao.addFavorite(dto);
		}
		return result;
	}
	@Override
	public FavoriteDto findFavoriteByItemAndUserId(FavoriteDto dto) {
		return dao.findFavoriteByItemAndUserId(dto);
	}
	
	@Override
	public int deleteFavorite(FavoriteDto dto) {
		return dao.deleteFavorite(dto);
	}

	@Override
	public List<HouseInfo> findAllUserFavorite(String userId) {
		List<FavoriteDto> favoriteList = findAllUserFavoriteByUserId(userId);
		List<HouseInfo> estateList = new ArrayList<>();
		for(FavoriteDto item : favoriteList) {
			if(item.getItemNo() == -1) {
				HouseInfo deletedItem = HouseInfo.builder()
										.name("삭제된 매물입니다.")
										.price("0")
										.floor(0)
										.area(0d)
										.regist_date("")
										.favorite_no(item.getFavoriteNo())
										.build();
				deletedItem.setFavorite_no(item.getFavoriteNo());
				estateList.add(deletedItem);
			}
			else {
				HouseInfo estateItem = estateService.findHouseInfoByItemNo(item.getItemNo());
				estateItem.setFavorite_no(item.getFavoriteNo());
				estateItem.setRegist_date(item.getRegistDate());
				estateList.add(estateItem);
			}
			
		}
		String uploadDir = "C:/upload/img/estate";
		System.out.println("list = "+estateList);
		for(HouseInfo dto : estateList) {
			UserImageDto imageDto = findApartImage(dto.getFile_no());
			System.out.println("now Dto = "+dto);
			System.out.println("file_no = "+dto.getFile_no());
			if(imageDto==null) {
				imageDto = UserImageDto.builder()
							.saveFolder("")
							.fixedName("noImage.png").build();
			}
			else {
				System.out.println("image Dto is not null = " + imageDto);
			}
			Path filePath = Paths.get(uploadDir, imageDto.getSaveFolder(), imageDto.getFixedName());
			log.error(filePath.toString());
			File file = new File(filePath.toString());
			byte[] fileBytes = null;
			try {
				fileBytes = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			String base64Image = Base64.encodeBase64String(fileBytes);
			dto.setBase64(base64Image);
		}
		return estateList;
	}

	@Override
	public UserImageDto findApartImage(Integer file_no) {
		return dao.findApartImage(file_no);
	}
	@Override
	public int deleteAllFavoriteItem(String userId) {
		return dao.deleteAllFavoriteItem(userId);
	}
	@Override
	public List<FavoriteDto> selectAllFavoriteItemByItemNo(Integer itemNo) {
		return dao.selectAllFavoriteItemByItemNo(itemNo);
	}
	@Override
	public int updateFavoriteItemsCauseDelete(List<FavoriteDto> list) {
		return dao.updateFavoriteItemsCauseDelete(list);
	}
	@Override
	public List<FavoriteDto> findAllUserFavoriteByUserId(String userId) {
		return dao.findAllUserFavoriteByUserId(userId);
	}

	
	
}
