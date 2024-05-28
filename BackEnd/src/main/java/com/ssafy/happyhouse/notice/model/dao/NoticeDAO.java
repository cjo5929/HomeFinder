package com.ssafy.happyhouse.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ssafy.happyhouse.notice.model.dto.NoticeDTO;

@Mapper
public interface NoticeDAO {
    List<NoticeDTO> getNoticeList(@Param("offset") int offset, @Param("limit") int limit);
    int getNoticeCount();
    List<NoticeDTO> searchNotice(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    int searchNoticeCount(@Param("keyword") String keyword);
    void insertNotice(NoticeDTO notice);
    NoticeDTO getNoticeById(int id);
    void updateNotice(NoticeDTO notice);
    void deleteNotice(int id);
}