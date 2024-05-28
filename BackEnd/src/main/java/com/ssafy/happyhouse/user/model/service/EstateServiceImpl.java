package com.ssafy.happyhouse.user.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.user.model.dao.EstateDao;
import com.ssafy.happyhouse.user.model.dao.FavoriteDao;
import com.ssafy.happyhouse.user.model.dto.FavoriteDto;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {
	private final EstateDao dao;
	private final FavoriteDao favoriteDao;
    private final String uploadDir = "C:/upload/img/estate";
	
	@Override
	@Transactional
	public int insertApart(HouseInfo dto, MultipartFile file) {
		try {
			if(file!=null) {
				String today = new SimpleDateFormat("yyMMdd").format(new Date());
				String uploadDir = "C:/upload/img/estate";
				String imgDirPath = uploadDir + File.separator+today;
	            File folder = new File(imgDirPath);
	            if (!folder.exists()) folder.mkdirs();
	            log.debug("img path : {}",imgDirPath);
	            if (!folder.exists()) folder.mkdirs();
	            String originalFileName = file.getOriginalFilename();
	            UserImageDto imageDto = new UserImageDto();
				if(!originalFileName.isEmpty()) {
					String fixedFileName = UUID.randomUUID().toString()+
							originalFileName.substring(originalFileName.lastIndexOf("."));
					imageDto.setOriginalName(originalFileName);
					imageDto.setFixedName(fixedFileName);
					imageDto.setSaveFolder(today);
					log.debug("실제 이름 : {} , 수정 이름 : {}",originalFileName,fixedFileName);
					file.transferTo(new File(folder, fixedFileName));
					dto.setImage(UserImageDto.builder()
							.originalName(originalFileName)
							.fixedName(fixedFileName)
							.saveFolder(today).
							type(dto.getType()).
							build());
				}
				dao.insertApartImage(dto);
				Integer lastFileNo = selectFileNo(dto);
				dto.setFile_no(lastFileNo);
				dao.insertApart(dto);
				return 1;
			}
			else {
				return dao.insertApart(dto);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return dao.insertApart(dto);
	}

	@Override
	public Integer selectFileNo(HouseInfo dto) {
		return dao.selectFileNo(dto);
	}

	@Override
	public List<HouseInfo> selectAllEstateItem(String userId) {
		System.out.println("user Id = "+userId);
		List<HouseInfo> estateList = dao.selectAllEstateItem(userId);
		List<UserImageDto> imageList = dao.selectAllEstateImage(userId);
		Map<Integer, UserImageDto> imageMap = new HashMap<>();
	    for (UserImageDto imageDto : imageList) {
	        imageMap.put(imageDto.getFile_no(), imageDto);
	    }
		for(HouseInfo dto : estateList) {
			String uploadDir = "C:/upload/img/estate";
			UserImageDto imageDto = null;
			if(dto.getFile_no() == null) {
				imageDto = UserImageDto.builder()
						.saveFolder("")
						.fixedName("noImage.png").build();
			}
			else {
				imageDto = imageMap.get(dto.getFile_no());
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
	public List<HouseInfo> selectAllEstateItem(Integer aptCode) {
		List<HouseInfo> list =  dao.selectAllEstateItemByAptCode(aptCode);
		for(HouseInfo dto : list) {
			Integer num = dto.getFile_no();
			UserImageDto imageDto = dao.selectFileInfoByFileNo(num);
			String uploadDir = "C:/upload/img/estate";
			Path filePath = null;
			if(imageDto != null) {
				filePath = Paths.get(uploadDir, imageDto.getSaveFolder(), imageDto.getFixedName());
			}
			else {
				filePath = Paths.get(uploadDir, "", "noImage.png");
			}
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
		return list;
	}

	@Override
	public List<UserImageDto> selectAllEstateImage(String userId) {
		return dao.selectAllEstateImage(userId);
	}

	@Override
	public Integer selectItemNo() {
		return dao.selectItemNo();
	}

	@Override
	public Integer findFileNoByItemNo(Integer itemNo) {
		return dao.findFileNoByItemNo(itemNo);
	}

	@Override
	@Transactional
	public int deleteApart(Integer itemNo) {
		Integer file_no = findFileNoByItemNo(itemNo);
		if(file_no != null) {
			deleteApartImage(file_no);
		}
		List<FavoriteDto> favoriteList = favoriteDao.selectAllFavoriteItemByItemNo(itemNo);
		if(favoriteList!=null && favoriteList.size()>0) {
			favoriteDao.updateFavoriteItemsCauseDelete(favoriteList);			
		}
		int result = dao.deleteApart(itemNo);
		return result;
	}

	@Override
	public int deleteApartImage(Integer fileNo) {
		return dao.deleteApartImage(fileNo);
	}

	@Override
	public List<HouseInfo> findAllEstateItemByUserId(String userId) {
		return dao.findAllEstateItemByUserId(userId);
	}

	@Override
	public int deleteAllUserItem(String userId) {
		return dao.deleteAllUserItem(userId);
	}

	@Override
	public HouseInfo findHouseInfoByItemNo(Integer itemNo) {
		return dao.findHouseInfoByItemNo(itemNo);
	}


	@Override
	@Transactional
	public int updateEstateImage(MultipartFile file, Integer fileNo) {
	    System.out.println("call update estate image");
	    System.out.println("Original filename: " + file.getOriginalFilename());
	    UserImageDto dbImage = selectEstateImageByFileNo(fileNo);
	    try {
	        String today = new SimpleDateFormat("yyMMdd").format(new Date());
	        String imgDirPath = uploadDir + File.separator + today;
	        File folder = new File(imgDirPath);
	        if (!folder.exists()) {
	            folder.mkdirs();
	            System.out.println("Created directory: " + imgDirPath);
	        }
	        UserImageDto dto = new UserImageDto();
	        String originalFileName = file.getOriginalFilename();
	        if (originalFileName != null && !originalFileName.isEmpty()) {
	            String fixedFileName = UUID.randomUUID().toString() +
	                    originalFileName.substring(originalFileName.lastIndexOf("."));
	            dto.setOriginalName(originalFileName);
	            dto.setFixedName(fixedFileName);
	            dto.setSaveFolder(today);
	            dto.setType(dbImage.getType());
	            dto.setFile_no(dbImage.getFile_no());
	            System.out.println("실제 이름 : " + originalFileName + ", 수정 이름 : " + fixedFileName);
	            File newFile = new File(folder, fixedFileName);
	            file.transferTo(newFile);
	            System.out.println("File created at: " + newFile.getAbsolutePath());
	        }
	        dao.updateEstateImage(dto);
	        String dbFolder = dbImage.getSaveFolder();
	        String dbFixedName = dbImage.getFixedName();
	        Path dbFilePath = Paths.get(uploadDir, dbFolder, dbFixedName);
	        System.out.println("Attempting to delete old file: " + dbFilePath);
	        if (Files.exists(dbFilePath)) {
	            Files.delete(dbFilePath);
	            System.out.println("Deleted old file: " + dbFilePath);
	        } else {
	            System.out.println("Old file not found: " + dbFilePath);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	@Override
	public UserImageDto selectEstateImageByFileNo(Integer fileNo) {
		return dao.selectEstateImageByFileNo(fileNo);
	}

	@Override
	public int updateEstateInfo(HouseInfo dto) {
		return dao.updateEstateInfo(dto);
	}


}
