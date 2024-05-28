package com.ssafy.happyhouse.news.model.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.happyhouse.news.model.dto.Article;
import com.ssafy.happyhouse.news.model.dto.NewsResponse;

@Service
public class NewsService {

	@Value("${naver.client.id}")
	private String clientId;

	@Value("${naver.client.secret}")
	private String clientSecret;

	private static final List<String> QUERIES = Arrays.asList("청약", "아파트");
	private static final int START = 1;
	private static final int DISPLAY = 50;

    // 클라이언트가 이 메서드를 호출하면 캐시된 데이터를 반환합니다.
	@Cacheable("news")
	public List<NewsResponse> getCachedNews() {
		return fetchNews();
	}

    // 12시간마다 캐시를 갱신하는 스케줄러
	@Scheduled(cron = "0 0 */12 * * *") // 12시간마다 캐시 갱신
	@CacheEvict(value = "news", allEntries = true)
	public void refreshNewsCache() {
		getCachedNews();
	}

	private List<NewsResponse> fetchNews() {
		List<NewsResponse> newsResponses = new ArrayList<>();

		for (String query : QUERIES) {
			String apiUrl = buildUrl(query, START, DISPLAY);
			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("X-Naver-Client-Id", clientId);
			requestHeaders.put("X-Naver-Client-Secret", clientSecret);

			String responseBody = get(apiUrl, requestHeaders);

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				NewsResponse newsResponse = objectMapper.readValue(responseBody, NewsResponse.class);
				if (newsResponse != null && newsResponse.getItems() != null) {
					List<Article> filteredArticles = new ArrayList<>();
					for (Article article : newsResponse.getItems()) {
						if (isNaverNews(article.getLink())) {
							List<String> imageUrls = getArticleImages(article.getLink());
							article.setImages(imageUrls);
							filteredArticles.add(article);
						}
					}
					if (!filteredArticles.isEmpty()) {
						newsResponse.setItems(filteredArticles);
						newsResponses.add(newsResponse);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return newsResponses;
	}

	private String buildUrl(String query, int start, int display) {
		try {
			String encodedQuery = URLEncoder.encode(query, "UTF-8");
			return "https://openapi.naver.com/v1/search/news.json?query=" + encodedQuery + "&start=" + start
					+ "&display=" + display;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("검색어 인코딩 실패", e);
		}
	}

	private boolean isNaverNews(String articleUrl) {
		return articleUrl.startsWith("https://n.news.naver.com/");
	}

	private List<String> getArticleImages(String articleUrl) {
		List<String> imageUrls = new ArrayList<>();

		if (isNaverNews(articleUrl)) {
			try {
				Document document = Jsoup.connect(articleUrl).get();

				// 썸네일 이미지 크롤링
				Element thumbnailElement = document.selectFirst("meta[property=og:image]");
				if (thumbnailElement != null) {
					String thumbnailUrl = thumbnailElement.attr("content");
					imageUrls.add(thumbnailUrl);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return imageUrls;
	}

	private static String get(String apiUrl, Map<String, String> requestHeaders) {
		HttpURLConnection con = connect(apiUrl);
		try {
			con.setRequestMethod("GET");
			for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
				con.setRequestProperty(header.getKey(), header.getValue());
			}

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				return readBody(con.getInputStream());
			} else {
				return readBody(con.getErrorStream());
			}
		} catch (IOException e) {
			throw new RuntimeException("API 요청과 응답 실패", e);
		} finally {
			con.disconnect();
		}
	}

	private static HttpURLConnection connect(String apiUrl) {
		try {
			URL url = new URL(apiUrl);
			return (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
		} catch (IOException e) {
			throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
		}
	}

	private static String readBody(InputStream body) {
		InputStreamReader streamReader = new InputStreamReader(body);

		try (BufferedReader lineReader = new BufferedReader(streamReader)) {
			StringBuilder responseBody = new StringBuilder();

			String line;
			while ((line = lineReader.readLine()) != null) {
				responseBody.append(line);
			}

			return responseBody.toString();
		} catch (IOException e) {
			throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
		}
	}
}