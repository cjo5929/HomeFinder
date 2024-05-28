package com.ssafy.happyhouse.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.happyhouse.notice.model.dto.NoticeDTO;
import com.ssafy.happyhouse.notice.model.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getNoticeList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<NoticeDTO> notices = noticeService.getNoticeList(page, size);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getNoticeCount() {
        int count = noticeService.getNoticeCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoticeDTO>> searchNotice(@RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<NoticeDTO> notices = noticeService.searchNotice(keyword, page, size);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/search/count")
    public ResponseEntity<Integer> searchNoticeCount(@RequestParam String keyword) {
        int count = noticeService.searchNoticeCount(keyword);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<Void> insertNotice(@RequestBody NoticeDTO notice) {
        noticeService.insertNotice(notice);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable int id) {
        NoticeDTO notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotice(@PathVariable int id, @RequestBody NoticeDTO notice) {
        notice.setId(id);
        noticeService.updateNotice(notice);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable int id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }
}
