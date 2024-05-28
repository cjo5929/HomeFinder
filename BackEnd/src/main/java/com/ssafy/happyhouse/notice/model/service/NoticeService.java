package com.ssafy.happyhouse.notice.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.happyhouse.notice.model.dao.NoticeDAO;
import com.ssafy.happyhouse.notice.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
	private final NoticeDAO noticeDAO;

	public List<NoticeDTO> getNoticeList(int page, int size) {
		int offset = (page - 1) * size;
		return noticeDAO.getNoticeList(offset, size);
	}

	public int getNoticeCount() {
		return noticeDAO.getNoticeCount();
	}

	public List<NoticeDTO> searchNotice(String keyword, int page, int size) {
		int offset = (page - 1) * size;
		return noticeDAO.searchNotice(keyword, offset, size);
	}

	public int searchNoticeCount(String keyword) {
		return noticeDAO.searchNoticeCount(keyword);
	}

	public void insertNotice(NoticeDTO notice) {
		noticeDAO.insertNotice(notice);
	}

	public NoticeDTO getNoticeById(int id) {
		return noticeDAO.getNoticeById(id);
	}

	public void updateNotice(NoticeDTO notice) {
		noticeDAO.updateNotice(notice);
	}

	public void deleteNotice(int id) {
		noticeDAO.deleteNotice(id);
	}
}
