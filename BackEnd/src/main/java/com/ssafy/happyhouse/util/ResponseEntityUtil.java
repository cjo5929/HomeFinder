package com.ssafy.happyhouse.util;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseEntityUtil {
	private final Map<String, Object> result;
	private final ObjectMapper mapper;
	private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
	
	public void write(HttpServletResponse response, String message) throws JsonProcessingException, IOException {
		result.put("message", message);
		ResponseEntity<Map<String, Object>> entity = new ResponseEntity<>(result, ResponseEntityUtil.STATUS);
		response.getWriter().write(mapper.writeValueAsString(entity.getBody()));
	}
}
