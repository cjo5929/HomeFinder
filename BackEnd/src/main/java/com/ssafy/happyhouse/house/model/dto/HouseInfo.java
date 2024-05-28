package com.ssafy.happyhouse.house.model.dto;

import com.ssafy.happyhouse.user.model.dto.UserImageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseInfo {
	/**
	 * HouseInfo 스키마.
	 * 
	 */
	private Integer apt_id;
	private Integer built_year;
	private String dong_code;
	private String dong_name;
	private String jibun;
	private String name;
	private Double lat;
	private Double lng;
	
	/**
	 * 추가 정보
	 */
	private Integer floor;
	private Double area;
	private Integer room;
	private Integer bath;
	private String info;
	private String price;
	private String address;
	private String user_id;
	private String type;
	private Integer file_no;
	private Integer item_no;
	private Integer favorite_no;
	
	
	private String move_in;
	private String heater;
	private String entrance;
	private String regist_date;
	
	private UserImageDto image;
	@ToString.Exclude
    private String base64;
}
