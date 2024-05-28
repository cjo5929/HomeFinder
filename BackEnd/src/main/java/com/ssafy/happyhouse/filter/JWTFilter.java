package com.ssafy.happyhouse.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happyhouse.user.model.dto.CustomUserDetails;
import com.ssafy.happyhouse.user.model.dto.UserDto;
import com.ssafy.happyhouse.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
	private final JWTUtil util;
	
	@Override
	// JWTFilter는 LoginFilter의 바로 앞에서 수행되는 Filter이기 때문에, JWT Token에 대한 인증이 실패하면 Login으로 넘어가고,
	// JWT Token에 대한 인증이 성공하면 Login 과정을 넘어가도 되는 것임.
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String accessToken = request.getHeader("Authorization");
		String refreshToken = request.getHeader("regenerateToken");
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        System.out.println("accessToken = "+accessToken);

        // "/member/force/logout" 또는 "/ws/chat" 경로의 경우 다음 필터로 넘어감
        if (requestURI.equals("/member/force/logout") || requestURI.startsWith("/ws/chat") || requestURI.startsWith("/member/check")
        		|| requestURI.startsWith("/member/mail") || requestURI.startsWith("/member/reset")) {
            filterChain.doFilter(request, response);
            return;
        }
		if(accessToken == null && refreshToken == null) {
			log.debug("현재 상황은 Login상황이다. 따라서 JWTFilter가 아닌 LoginFilter가 처리해야한다.");
			filterChain.doFilter(request, response);
			return;
		}
		
		// refreshToken 검증
		// refreshToken을 먼저 검증하는 이유는 사용자가 어떠한 요청을 보낼 땐 accessToken만을 보내기 때문임
		// 즉, refreshToken을 보낸다는 의미는 어차피 accessToken은 보내지 않았을 것이고
		// accessToken에 대한 새로운 발급 요청을 한다는 것.
		if (refreshToken != null && util.checkToken(refreshToken)) {
		    log.info("RefreshToken is valid. Reissuing AccessToken.");
		    System.out.println("refreshToken = "+refreshToken);
		    System.out.println("accessToken = "+accessToken);
		    // AccessToken 재발급 로직 추가
		    String userId = util.getUserId(refreshToken);
		    String  userRole = util.getUserRole(refreshToken);
		    String newAccessToken = util.createAccessToken(userId, userRole);
		    System.out.println("AccessToken 재발행."+newAccessToken);
		    // 응답 헤더에 새로 발급된 AccessToken 추가
		    response.setHeader("Authorization", "Bearer " + newAccessToken);
		    
		    response.setContentType("application/json");
		    ObjectMapper mapper = new ObjectMapper();
		    Map<String, Object> map = new HashMap<>();
		    map.put("accessToken", newAccessToken);
		    String json = mapper.writeValueAsString(map);
		    response.setCharacterEncoding("UTF-8");
		    response.setStatus(HttpStatus.OK.value());
		    response.getWriter().write(json);
		    response.getWriter().flush();
		    refreshToken = null;
		    return;
		}
		
		// 현재 문제 상황은 accessToken, refreshToken 둘중 하나만을 갖고 보내는 것이라
		// access가 만료되었다면 refresh의 검증이 넘어가는게 아닌 바로 두 토큰 모두 유효가 풀린것으로 된 것임.
		        // 그럼 refresh를 위로 올린다...?
		        // 
		
		// accessToken 검증
        if (accessToken != null) {
        	if(util.checkToken(accessToken)) {
        		log.error("현재 상황은 AccessToken의 유효가 남아있어 성공했을 때.");
                setAuthentication(accessToken);
                filterChain.doFilter(request, response);
                return;
        	}
        	else {
        		log.error("AccessToken은 null이 아니게 잘 보냈지만, 유횩간이 다 되었다.");
        		ObjectMapper mapper = new ObjectMapper();
        		Map<String, Object> map = new HashMap<>();
        		map.put("message", "AccessToken의 유효기간 만료. 새 토큰 발행 필요");
        		String json = mapper.writeValueAsString(map);
        		response.setContentType("application/json");
        		response.setStatus(HttpStatus.FORBIDDEN.value());
        		response.setCharacterEncoding("UTF-8");
        		response.getWriter().write(json);
        		response.getWriter().flush();
        		return;
        	}
        }
 

        // accessToken과 refreshToken이 모두 유효기간이 만료된 경우
        log.error("Both tokens are expired");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage = "Unauthorized access. Please login again.";
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", errorMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(responseJson);
        return;
    }
	private void setAuthentication(String token) {
        String userId = util.getUserId(token);
        String userRole = util.getUserRole(token);
        UserDto dto = UserDto.builder().userId(userId).userRole(userRole).userPassword("dummyPassword").build();
        CustomUserDetails details = new CustomUserDetails(dto);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole));
        Authentication authToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authenticated user: {} with roles: {}", userId, details.getAuthorities());
    }
}
