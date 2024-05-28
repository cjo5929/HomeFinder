package com.ssafy.happyhouse.house.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FlaskController {

	@Value("${flask.server.url}")
	private String flaskServerUrl;

	@PostMapping("/predict")
	public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, Object> requestData) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);

		ResponseEntity<Map> response = restTemplate.exchange(flaskServerUrl + "/predict", HttpMethod.POST, entity,
				Map.class);

		Map<String, Object> responseBody = response.getBody();
		return ResponseEntity.ok(responseBody);
	}
}
