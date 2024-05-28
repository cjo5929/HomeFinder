package com.ssafy.happyhouse.news.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.happyhouse.news.model.dto.NewsResponse;
import com.ssafy.happyhouse.news.model.service.NewsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NewsController {

	private final NewsService newsService;

	@GetMapping("/api/news")
	public List<NewsResponse> getNews() {
		return newsService.getCachedNews();
	}
}