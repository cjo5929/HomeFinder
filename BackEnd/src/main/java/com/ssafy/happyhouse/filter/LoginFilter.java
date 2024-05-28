package com.ssafy.happyhouse.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happyhouse.user.model.dto.CustomUserDetails;
import com.ssafy.happyhouse.user.model.service.UserService;
import com.ssafy.happyhouse.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
	// 해당 멤버들은 UsernamePasswordAuthenticationFilter의 내부에 존재하는 멤버 변수를 새롭게 재정의 한 것
	// 그 이유는 아래의 "obtainUsername(request)"에서 obtainUsername의 메서드가 다음과 같기 때문임
	// obtainUsername --> return request.getParameter(usernameParameter)
	// 기존에는 "username", "password"로 설정되어있어서 내가 편하도록 바꾼 것
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "userId";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "userPassword";
	private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
	
	// AuthenticationManager는 인증 요청이 들어올 때 인증을 진행하는 Manager임. DispatcherServlet과 비슷한 역할.
	private final AuthenticationManager manager;
	private final JWTUtil jwtUtil;
	public LoginFilter(AuthenticationManager manager, JWTUtil jwtUtil) {
		this.manager = manager;
		this.jwtUtil = jwtUtil;
		// 이 필터가 작동해야할 url을 설정한 것
		// default는 localhost:8080/login 즉 "/login"
		setFilterProcessesUrl("/member/login");	
	}
	
	// Spring Security는 Filter 기반으로 작동을 함. 즉, Dispatcher Servlet에 도착하기 이전에 인가, 인증에 대한 작업을 처리.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException{
		System.out.println("call attempt Authentication");
		// obtainUsername은 request.getParameter("usrname")과 같은 원리.
		String username = obtainUsername(request);
		String userPassword = obtainPassword(request);
		
		System.out.println("user name = "+username);
		System.out.println("user password = "+userPassword);
		
		// token으로 생성
		// Manager가 인증을 시작하기 위해서는 Token 형태로 보내줘야함.
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, userPassword, null);
		// manager가 해당 토큰을 받고 검증 시작.
		return manager.authenticate(token);
	}
	
	// 인증에 성공을 했다면 진행하는 사항.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("success");
		CustomUserDetails userDetails = (CustomUserDetails)authResult.getPrincipal();
		String userName = userDetails.getUsername();
		Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String userRole = auth.getAuthority();
		System.out.println("로그인 인증 성공 후 name과 role = "+userName+"///"+userRole);
		String accessToken = jwtUtil.createAccessToken(userName, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userName, userRole);
		
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		
		response.setStatus(status.value());
		response.setContentType("application/json");
		
		map.put("accessToken", accessToken);
		map.put("refreshToken", refreshToken);
		map.put("userId", userName);
		map.put("userRole", userRole);
		
		ResponseEntity<Map<String,Object>> responseEntity = new ResponseEntity<>(map, status);
		response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
	}
	// 인증에 실패를 했다면 진행하는 사항.
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		System.out.println("fail");
		response.setStatus(401);
	}
	
	@Nullable
	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(this.passwordParameter);
	}
	@Nullable
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(this.usernameParameter);
	}
}
