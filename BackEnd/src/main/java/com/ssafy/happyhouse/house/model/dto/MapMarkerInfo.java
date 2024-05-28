package com.ssafy.happyhouse.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapMarkerInfo {
    private int aptCode;
    private String apartmentName;
    private double lng;
    private double lat;
    
}
