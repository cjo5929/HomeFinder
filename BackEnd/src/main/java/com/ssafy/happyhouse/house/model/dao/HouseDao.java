package com.ssafy.happyhouse.house.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.happyhouse.house.model.dto.AddressName;
import com.ssafy.happyhouse.house.model.dto.ApplyHome;
import com.ssafy.happyhouse.house.model.dto.EducationDataDTO;
import com.ssafy.happyhouse.house.model.dto.FacilitiesDataDTO;
import com.ssafy.happyhouse.house.model.dto.HouseDeal;
import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.house.model.dto.MapMarkerInfo;
import com.ssafy.happyhouse.house.model.dto.PopulationDataDTO;
import com.ssafy.happyhouse.house.model.dto.SafetyScore;

@Mapper
public interface HouseDao {

	// 아파트명으로 아파트 정보를 검색
	List<HouseDeal> getHouseListByName(String apartmentName);

	// 아파트 코드를 통해 아파트 상세 정보 조회
	List<HouseDeal> getHouseDetailByAptCode(int aptId);

	// 위도 경도를 기반으로 아파트 리스트 조회
	List<HouseDeal> getHouseListByLatLng(Map<String, Object> map);

	// 주소를 기반으로 모든 아파트 정보를 조회
	List<MapMarkerInfo> getApartListByAddressName(AddressName addressName);

	// 시 목록 조회
	List<String> findSidoList();

	// 시를 기반으로 구 군 목록 조회
	List<String> findGugunList(String sidoName);

	// 시, 구 군을 기반으로 동 목록 조회
	List<String> findDongList(Map<String, String> params);

	// aptCode로 아파트 정보 상세조회
	Map<String, Object> getAptInfoDetailByAptCode(int aptCode);

	// aptCode로 아파트 매물 리스트 조회
	List<HouseDeal> getHouseDealListByAptCode(int aptCode);

    List<HouseInfo> getApartNameByDongCode(String dongCode);
	// 안전 점수 리스트 조회
	List<SafetyScore> getSafetyScoreList();

	// 인구수 리스트 조회
	List<PopulationDataDTO> getPopulationList();

	// 교육 리스트 조회
	List<EducationDataDTO> getEducationList();

	// 편의시설 리스트 조회
	List<FacilitiesDataDTO> getFacilitiesList();
	
	// 청약 조회 
	List<ApplyHome> getApplyHomeDataByMonth(String yearMonth);
}
