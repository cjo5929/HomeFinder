package com.ssafy.happyhouse.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyScore {
    private String sido;
    private String gugun;
    private Long trafficAccidents;
    private Long totalReports;
    private Long population;
    private Double policeStations;
    private Double cctvCount;
    private Double safetyScore;
    private String dongCode;
}