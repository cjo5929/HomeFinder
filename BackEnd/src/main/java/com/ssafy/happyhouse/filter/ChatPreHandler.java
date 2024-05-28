package com.ssafy.happyhouse.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ssafy.happyhouse.user.model.dto.CustomUserDetails;
import com.ssafy.happyhouse.user.model.dto.UserDto;
import com.ssafy.happyhouse.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {
    private final JWTUtil util;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (headerAccessor != null && headerAccessor.getCommand() != null) {
            String userId = (String) headerAccessor.getSessionAttributes().get("userId");
            String userRole = (String) headerAccessor.getSessionAttributes().get("userRole");
            if (userId != null && userRole != null) {
                setAuthentication(userId, userRole);
            } else {
                log.error("Invalid or missing user information in session attributes");
                // JWT 토큰이 유효하지 않으면 메시지를 처리하지 않음
                return null;
            }
        }
        return message;
    }

    private void setAuthentication(String userId, String userRole) {
        UserDto dto = UserDto.builder().userId(userId).userRole(userRole).userPassword("dummyPassword").build();
        CustomUserDetails details = new CustomUserDetails(dto);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole));
        Authentication authToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authenticated user: {} with roles: {}", userId, details.getAuthorities());
    }
}
