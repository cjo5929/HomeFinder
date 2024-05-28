package com.ssafy.happyhouse.user.model.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
	private final UserDto user;
	public CustomUserDetails(UserDto user) {
		this.user = user;
	}
	
	// 사용자의 role 반환
	// AuthenticationManager가 인증 검사를 하기 위한 클래스의 내용들임.
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(user.getUserRole()));
        return list;
    }

	@Override
	public String getPassword() {
		return user.getUserPassword();
	}
	

	@Override
	public String getUsername() {
		return user.getUserId();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
