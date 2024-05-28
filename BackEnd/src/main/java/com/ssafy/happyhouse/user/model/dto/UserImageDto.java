package com.ssafy.happyhouse.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserImageDto {
	private String originalName;
	private String fixedName;
	private String saveFolder;
	private Integer file_no;
	private String type;
}
