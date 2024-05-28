package com.ssafy.happyhouse.house.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.happyhouse.house.model.dto.AddressName;
import com.ssafy.happyhouse.house.model.dto.ApplyHome;
import com.ssafy.happyhouse.house.model.dto.EducationDataDTO;
import com.ssafy.happyhouse.house.model.dto.FacilitiesDataDTO;
import com.ssafy.happyhouse.house.model.dto.HouseDeal;
import com.ssafy.happyhouse.house.model.dto.HouseInfo;
import com.ssafy.happyhouse.house.model.dto.MapMarkerInfo;
import com.ssafy.happyhouse.house.model.dto.PopulationDataDTO;
import com.ssafy.happyhouse.house.model.dto.SafetyScore;
import com.ssafy.happyhouse.house.model.service.HouseService;
import com.ssafy.happyhouse.user.model.service.EstateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

	private final HouseService houseService;
	private final EstateService estateService;

	/**
	 * 아파트 코드를 통해 아파트 상세 정보 조회
	 * 
	 * @param aptCode
	 * @return
	 */
	@GetMapping("/detail/{apt_id}")
	public ResponseEntity<?> findDetail(@PathVariable("apt_id") int aptId) {

		List<HouseDeal> findDetail = houseService.findHouseDealByAptCode(aptId);

		if (findDetail.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return ResponseEntity.ok(findDetail);
	}

	/**
	 * 아파트명으로 아파트 정보를 검색
	 * 
	 * @param apartName
	 * @return
	 */
	@GetMapping("/list/name/{name}")
	public ResponseEntity<?> findApartListByName(@PathVariable("name") String apartName) {

		List<HouseDeal> findList = houseService.findHouseDealByName(apartName);

		if (findList.isEmpty())
			return ResponseEntity.ok(Collections.emptyList());

		return ResponseEntity.ok(findList);
	}

	/**
	 * 주소를 기반으로 모든 아파트 정보를 조회
	 * 
	 * @param addressName
	 * @return
	 */
	@GetMapping("/list/apart")
	public ResponseEntity<?> findApartListByAddressName(@ModelAttribute AddressName addressName) {

		List<MapMarkerInfo> findList = houseService.findApartListByAddressName(addressName);
		return ResponseEntity.ok(findList);
	}

	/**
	 * aptCode 기반으로 아파트 상세 조회
	 * 
	 * @param aptCode
	 * @return
	 */
	@GetMapping("/list/{aptCode}")
	public ResponseEntity<Map<String, Object>> getAptInfoDetailByAptCode(@PathVariable int aptCode) {
		Map<String, Object> aptInfoDetail = houseService.getAptInfoDetailByAptCode(aptCode);
		return ResponseEntity.ok(aptInfoDetail);
	}
	
	@GetMapping("/list/dongCode/{dongCode}")
	public ResponseEntity<?> getApartNameByDongCode(@PathVariable("dongCode") String dongCode){
		System.out.println("received data = "+dongCode);
		HttpStatus status = HttpStatus.ACCEPTED;
		List<HouseInfo> list = houseService.getApartNameByDongCode(dongCode);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		return new ResponseEntity<Map<String,Object>>(map, status);
	}

	/**
	 * 시/도 목록 조회
	 *
	 * @return
	 */
	@GetMapping("/sido")
	public ResponseEntity<?> findSidoList() {
		List<String> sidoList = houseService.findSidoList();
		return ResponseEntity.ok(sidoList);
	}

	/**
	 * 구/군 목록 조회
	 *
	 * @param sidoName
	 * @return
	 */
	@GetMapping("/gugun")
	public ResponseEntity<?> findGugunList(@RequestParam("sidoName") String sidoName) {
		List<String> gugunList = houseService.findGugunList(sidoName);
		return ResponseEntity.ok(gugunList);
	}

	/**
	 * 동 목록 조회
	 *
	 * @param sidoName
	 * @param gugunName
	 * @return
	 */
	@GetMapping("/dong")
	public ResponseEntity<?> findDongList(@RequestParam("sidoName") String sidoName,
			@RequestParam("gugunName") String gugunName) {
		List<String> dongList = houseService.findDongList(sidoName, gugunName);
		return ResponseEntity.ok(dongList);
	}

	/**
	 * aptCode 기반으로 아파트 매물 리스트 조회
	 * 
	 * @param aptCode
	 * @return
	 */

	@GetMapping("/deals/{aptCode}")
	public ResponseEntity<List<HouseDeal>> getHouseDealListByAptCode(@PathVariable int aptCode) {
		List<HouseDeal> houseDealList = houseService.getHouseDealListByAptCode(aptCode);
		System.out.println(houseDealList.size());
		return ResponseEntity.ok(houseDealList);
	}
	
	@GetMapping("/estate/list/{aptCode}")
	public ResponseEntity<?> getEstateApartListByAptCode(@PathVariable Integer aptCode){
		System.out.println("aptCode = "+aptCode);
		List<HouseInfo> estateList = estateService.selectAllEstateItem(aptCode);
		System.out.println(estateList);
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> map = new HashMap<>();
		map.put("list", estateList);
		return new ResponseEntity<Map<String,Object>>(map, status);
	}

	/**
	 * 안전 점수 리스트 조회
	 * 
	 * @return
	 */
	@GetMapping("/scores")
	public ResponseEntity<List<SafetyScore>> getSafetyScoreList() {
		List<SafetyScore> safetyScoreList = houseService.getSafetyScoreList();
		return ResponseEntity.ok(safetyScoreList);
	}

	/**
	 * 인구수 리스트 조회
	 * 
	 * @return
	 */
	@GetMapping("/population")
	public ResponseEntity<List<PopulationDataDTO>> getPopulationList() {
		List<PopulationDataDTO> list = houseService.getPopulationList();
		return ResponseEntity.ok(list);
	}

	/**
	 * 교육 리스트 조회
	 * 
	 * @return
	 */
	@GetMapping("/education")
	public ResponseEntity<List<EducationDataDTO>> getEducationList() {
		List<EducationDataDTO> list = houseService.getEducationList();
		return ResponseEntity.ok(list);
	}

	/**
	 * 편의시설 리스트 조회
	 * 
	 * @return
	 */
	@GetMapping("/fac")
	public ResponseEntity<List<FacilitiesDataDTO>> getFacilitiesList() {
		List<FacilitiesDataDTO> list = houseService.getFacilitiesList();
		return ResponseEntity.ok(list);
	}
	
	
	/**
	 * 청약APT 리스트 조회
	 * 
	 * @return
	 */
    @GetMapping("/apply-home")
    public ResponseEntity<List<ApplyHome>> getApplyHomeDataByMonth(@RequestParam String yearMonth) {
        return ResponseEntity.ok(houseService.getApplyHomeDataByMonth(yearMonth));
    }

}