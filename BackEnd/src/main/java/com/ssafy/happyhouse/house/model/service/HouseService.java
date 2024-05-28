package com.ssafy.happyhouse.house.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.happyhouse.house.model.dao.HouseDao;
import com.ssafy.happyhouse.house.model.dto.AddressName;
import com.ssafy.happyhouse.house.model.dto.ApplyHome;
import com.ssafy.happyhouse.house.model.dto.EducationDataDTO;
import com.ssafy.happyhouse.house.model.dto.FacilitiesDataDTO;
import com.ssafy.happyhouse.house.model.dto.HouseDeal;
import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.house.model.dto.MapMarkerInfo;
import com.ssafy.happyhouse.house.model.dto.PopulationDataDTO;
import com.ssafy.happyhouse.house.model.dto.SafetyScore;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HouseService {

	private final HouseDao houseDao;

	/**
	 * 아파트 코드를 통해 아파트 상세 정보 조회
	 * 
	 * @param aptCode
	 * @return
	 */
	public List<HouseDeal> findHouseDealByAptCode(int aptId) {

		List<HouseDeal> findHouseDeal = houseDao.getHouseDetailByAptCode(aptId);

		return findHouseDeal;
	}

	/**
	 * 동 코드로 아파트 검색
	 * 
	 *
	 */
	public List<HouseInfo> getApartNameByDongCode(String dongCode) {
		return houseDao.getApartNameByDongCode(dongCode);
	}

	/**
	 * 아파트명으로 아파트 정보를 검색
	 * 
	 * @param apartName
	 * @return
	 */
	public List<HouseDeal> findHouseDealByName(String apartName) {

		List<HouseDeal> findHouseList = houseDao.getHouseListByName(apartName);

		return findHouseList;
	}

	/**
	 * 주소를 기반으로 모든 아파트 정보를 조회
	 * 
	 * @param addressName
	 * @return
	 */
	public List<MapMarkerInfo> findApartListByAddressName(AddressName addressName) {
		return houseDao.getApartListByAddressName(addressName);
	}

	/**
	 * 시 목록 조회
	 * 
	 * @param addressName
	 * @return
	 */
	public List<String> findSidoList() {
		return houseDao.findSidoList();
	}

	/**
	 * 시를 기반으로 구 군 목록 조회
	 * 
	 * @param sidoName
	 * @return
	 */

	public List<String> findGugunList(String sidoName) {
		return houseDao.findGugunList(sidoName);
	}

	/**
	 * 시, 구 군을 기반으로 동 목록 조회
	 * 
	 * @param sidoName, gugunName
	 * @return
	 */

	public List<String> findDongList(String sidoName, String gugunName) {
		Map<String, String> params = new HashMap<>();
		params.put("sidoName", sidoName);
		params.put("gugunName", gugunName);
		return houseDao.findDongList(params);
	}

	/**
	 * aptCode 기반으로 아파트 상세 조회
	 * 
	 * @param aptCode
	 * @return
	 */
	public Map<String, Object> getAptInfoDetailByAptCode(int aptCode) {
		return houseDao.getAptInfoDetailByAptCode(aptCode);
	}

	/**
	 * aptCode 기반으로 아파트 매물 리스트 조회
	 * 
	 * @param aptCode
	 * @return
	 */
	public List<HouseDeal> getHouseDealListByAptCode(int aptCode) {
		return houseDao.getHouseDealListByAptCode(aptCode);
	}

	/**
	 * 안전 점수 리스트 조회
	 * 
	 * @return
	 */
	public List<SafetyScore> getSafetyScoreList() {

		return houseDao.getSafetyScoreList();
	}

	/**
	 * 인구수 리스트 조회
	 * 
	 * @return
	 */
	public List<PopulationDataDTO> getPopulationList() {

		return houseDao.getPopulationList();
	}

	/**
	 * 교육 리스트 조회
	 * 
	 * @return
	 */
	public List<EducationDataDTO> getEducationList() {

		return houseDao.getEducationList();
	}

	/**
	 * 편의시설 리스트 조회
	 * 
	 * @return
	 */
	public List<FacilitiesDataDTO> getFacilitiesList() {

		return houseDao.getFacilitiesList();
	}

	/**
	 * 청약 리스트 조회
	 * 
	 * @return
	 */
	public List<ApplyHome> getApplyHomeDataByMonth(String yearMonth) {
		return houseDao.getApplyHomeDataByMonth(yearMonth);
	}

}