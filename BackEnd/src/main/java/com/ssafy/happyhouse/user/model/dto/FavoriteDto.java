package com.ssafy.happyhouse.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {
	private Integer favoriteNo;
	private String userId;
	private Integer itemNo;
	private String itemType;
	private String registDate;
}
