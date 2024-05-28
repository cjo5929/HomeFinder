package com.ssafy.happyhouse.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopulationDataDTO {
    private String gugun;
    private Long count;
    private String dongCode;

    // Getters and Setters
}