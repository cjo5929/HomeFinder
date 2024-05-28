package com.ssafy.happyhouse.user.model.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.happyhouse.user.model.dao.UserDao;
import com.ssafy.happyhouse.user.model.dto.CustomUserDetails;
import com.ssafy.happyhouse.user.model.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserDao dao;
	
	public CustomUserDetailsService(UserDao dao) {
		this.dao = dao;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto user = null;
		user = dao.findById(username);
		System.out.println("user = "+user);
		if(user!=null) {
			log.info("user : {}", user);
			return new CustomUserDetails(user);
		}
		else {
			return null;
		}
	}
	
}
