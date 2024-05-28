package com.ssafy.happyhouse.notice.model.dto;


import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
    private int id;
    private String title;
    private String content;
    private String user_id;
    private Timestamp created_at;
    private Timestamp updated_at;
}