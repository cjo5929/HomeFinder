package com.ssafy.happyhouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ssafy.happyhouse.filter.JWTFilter;
import com.ssafy.happyhouse.filter.LoginFilter;
import com.ssafy.happyhouse.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final AuthenticationConfiguration config;
	private final JWTUtil jwtUtil;

	@Bean
	public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration config) throws Exception {
	    System.out.println("get Authentication Manager");
	    return config.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf((auth) -> auth.disable());
	    http.formLogin((auth) -> auth.disable());
	    http.httpBasic((auth) -> auth.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
	    http.authorizeHttpRequests((auth) -> auth
	    		
	            .requestMatchers("/member").permitAll()
	            .requestMatchers("/member/login").permitAll()
	            .requestMatchers("/member/check/**").permitAll()
	            .requestMatchers("/member/mail/**").permitAll()
	            .requestMatchers("/member/reset/**").permitAll()
	            .requestMatchers("/member/force/logout").permitAll()
	            .requestMatchers("/admin").hasRole("ADMIN")
	            .requestMatchers("/uploads/**").permitAll()
	            .requestMatchers("/house/**").permitAll()
	            .requestMatchers("/api/**").permitAll()
	            .requestMatchers("/notice/**").permitAll()
	            .requestMatchers("/favorite/**").hasAnyRole("ESTATE", "USER")
	            .requestMatchers("/ws/chat/**").permitAll()
	            .requestMatchers("/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
	            .anyRequest().authenticated());
	    
	    http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
	    http.addFilterAt(new LoginFilter(getAuthenticationManager(config), jwtUtil), UsernamePasswordAuthenticationFilter.class);

	    http.sessionManagement((session) -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

	    // CORS 설정 추가
	    http.cors().and();

	    return http.build();
	}
	// CORS 설정을 위한 Bean 등록
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
//	    configuration.addAllowedOrigin("http://localhost:5173"); // 허용할 오리진 설정
	    configuration.addAllowedMethod("*"); // 허용할 HTTP 메서드 설정
	    configuration.addAllowedHeader("*"); // 허용할 헤더 설정
	    configuration.setAllowCredentials(true); // 자격증명 허용 설정

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 설정 적용
	    return source;
	}
}
