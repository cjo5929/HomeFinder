import { localAxios } from "@/util/http-commons.js";

const local = localAxios();

// 뉴스 데이터 조회
function getNewsList(success, fail) {
  local.get(`/api/news`).then(success).catch(fail);
}

export {
  getNewsList, // 추가된 함수 내보내기
};
