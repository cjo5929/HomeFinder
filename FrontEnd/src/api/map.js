import { localAxios, mapAxios, flaskAxios } from '@/util/http-commons.js';

const local = mapAxios();
const estate = localAxios();
const flask = flaskAxios();

// 동 코드로 아파트 거래 내역 조회
function getApartListByDongCode(dongCode, success, fail) {
  local.get(`/list/dongCode/${dongCode}`).then(success).catch(fail);
}

// 아파트 매물 리스트
function getHouseDealListByAptCode(aptCode, success, fail) {
  local.get(`/deals/${aptCode}`).then(success).catch(fail);
}

function getHouseDealListByAptCode2(aptCode) {
  return local
    .get(`/deals/${aptCode}`)
    .then((response) => response)
    .catch((error) => {
      throw error;
    });
}

// 상세정보
function getAptInfoDetailByAptCode(aptCode, success, fail) {
  local.get(`/list/${aptCode}`).then(success).catch(fail);
}

function getEstateApartListByAptCode(aptCode, success, fail) {
  console.log('param =', aptCode);
  local.get(`/estate/list/${aptCode}`).then(success).catch(fail);
}

// 아파트 코드로 상세 정보 조회
function getApartDetail(aptId, success, fail) {
  local.get(`/detail/${aptId}`).then(success).catch(fail);
}

// 아파트 이름으로 아파트 정보 검색
function getApartListByName(name, success, fail) {
  local.get(`/list/name/${name}`).then(success).catch(fail);
}

// 주소로 모든 아파트 정보 조회
function getApartListByAddress(addressName, success, fail) {
  local.get(`/list/apart`, { params: addressName }).then(success).catch(fail);
}

// 시/도 목록 조회
function getSidoList(success, fail) {
  local.get(`/sido`).then(success).catch(fail);
}

// 구/군 목록 조회
function getGugunList(sidoName, success, fail) {
  local.get(`/gugun`, { params: { sidoName } }).then(success).catch(fail);
}

// 동 목록 조회
function getDongList(sidoName, gugunName, success, fail) {
  local.get(`/dong`, { params: { sidoName, gugunName } }).then(success).catch(fail);
}

// 안전점수 조회
function getSafetyScore(success, fail) {
  local.get(`/scores`).then(success).catch(fail);
}

// 인구수 조회
function getPopulationList(success, fail) {
  local.get(`/population`).then(success).catch(fail);
}

// 교육 조회
function getEducationList(success, fail) {
  local.get(`/education`).then(success).catch(fail);
}

// 편의시설 조회
function getFacilitiesList(success, fail) {
  local.get(`/fac`).then(success).catch(fail);
}

// Flask 서버에서 예측 데이터 받아오기
function getPrediction(data, success, fail) {
  flask.post('/predict', data).then(success).catch(fail);
}

// 청약 APT 정보 조회
function getApplyHomeDataByMonth(yearMonth, success, fail) {
  local.get(`/apply-home`, { params: { yearMonth } }).then(success).catch(fail);
}

export {
  getApartListByDongCode,
  getApartDetail,
  getApartListByName,
  getApartListByAddress,
  getSidoList,
  getGugunList,
  getDongList,
  getAptInfoDetailByAptCode,
  getHouseDealListByAptCode,
  getHouseDealListByAptCode2,
  getEstateApartListByAptCode,
  getSafetyScore,
  getPopulationList,
  getEducationList,
  getFacilitiesList,
  getPrediction,
  getApplyHomeDataByMonth,
};
