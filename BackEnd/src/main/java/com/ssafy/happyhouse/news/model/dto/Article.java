package com.ssafy.happyhouse.news.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
    private List<String> images;
}