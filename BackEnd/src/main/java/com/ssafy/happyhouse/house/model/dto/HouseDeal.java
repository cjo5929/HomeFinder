package com.ssafy.happyhouse.house.model.dto;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseDeal {
	
	private int dealId;
    private int aptCode;
    private String apartmentName;
    private String dealAmount;
    private int buildYear;
    private String dealDate;
    private String roadName;
    private String dongCode;
    private double exclusiveArea;
    private String jibun;
    private String floor;

}
