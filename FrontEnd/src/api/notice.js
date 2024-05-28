import { localAxios } from '@/util/http-commons.js';

const local = localAxios();

// 공지사항 목록 조회
function getNoticeList(params, success, fail) {
  local.get('/notice', { params }).then(success).catch(fail);
}

// 공지사항 등록
function insertNotice(noticeData, success, fail) {
  local.post('/notice', noticeData).then(success).catch(fail);
}

// 공지사항 수정
function updateNotice(noticeId, noticeData, success, fail) {
  local.put(`/notice/${noticeId}`, noticeData).then(success).catch(fail);
}

// 공지사항 id조회
function getNoticeById(noticeId, success, fail) {
  local.get(`/notice/${noticeId}`).then(success).catch(fail);
}

// 공지사항 삭제
function deleteNotice(noticeId, success, fail) {
  local.delete(`/notice/${noticeId}`).then(success).catch(fail);
}

// 공지사항 검색
function searchNotice(params, success, fail) {
  local.get('/notice/search', { params }).then(success).catch(fail);
} // 공지사항 총 개수 조회
function getNoticeCount(params, success, fail) {
  local.get('/notice/count', { params }).then(success).catch(fail);
}

export { getNoticeList, insertNotice, updateNotice, deleteNotice, getNoticeById, searchNotice, getNoticeCount };
