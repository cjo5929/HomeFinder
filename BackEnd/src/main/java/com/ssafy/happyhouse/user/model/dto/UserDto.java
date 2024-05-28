package com.ssafy.happyhouse.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	String userId;
	String userPassword;
	String userEmail;
	String userPhone;
	String userAddress;
	String registDate;
	String modifiedDate;
	String userRole;
	String refreshToken;
	UserImageDto userImageDto;
}
