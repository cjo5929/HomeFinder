<template>
  <div class="container mt-5 mb-4">
    <div class="event-detail card shadow-sm mb-5">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h2 class="card-title mb-0">{{ eventDetail.title }}</h2>
        <router-link to="/Subscription" class="btn btn-sm btn-secondary">돌아가기</router-link>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-6">
            <h4>청약 정보</h4>
            <table class="table table-striped">
              <tbody>
                <tr>
                  <th scope="row">주택 번호</th>
                  <td>{{ eventDetail.no }}</td>
                </tr>
                <tr>
                  <th scope="row">주소</th>
                  <td>{{ eventDetail.address }}</td>
                </tr>
                <tr>
                  <th scope="row">공급규모</th>
                  <td>{{ eventDetail.suply }}</td>
                </tr>
                <tr>
                  <th scope="row">지역</th>
                  <td>{{ eventDetail.area }}</td>
                </tr>
                <tr>
                  <th scope="row">당첨 발표일</th>
                  <td>{{ formatDate(eventDetail.presnatn) }}</td>
                </tr>
                <tr>
                  <th scope="row">건설사</th>
                  <td>{{ eventDetail.entrps }}</td>
                </tr>
              </tbody>
            </table>
            <a :href="eventDetail.url" class="btn btn-primary btn-sm" target="_blank">자세히 보기</a>
          </div>
          <div class="col-md-6">
            <h4><strong>AI에게 물어보는 내 당첨 예측</strong></h4>
            <form @submit.prevent="fetchPrediction">
              <div class="form-group">
                <label for="age">나이</label>
                <input v-model="formData.age" id="age" type="number" readonly class="form-control" />
              </div>
              <div class="form-group">
                <label for="reside_area">거주 지역</label>
                <input v-model="formData.reside_area" id="reside_area" type="text" class="form-control" />
              </div>
              <div class="form-group">
                <label for="score">점수</label>
                <input
                  v-model="formData.score"
                  id="score"
                  type="number"
                  class="form-control"
                  placeholder="아래 가점 계산기로 점수를 계산해주세요."
                  readonly
                />
              </div>
              <button type="submit" class="btn btn-custom btn-block mt-3">예측하기</button>
            </form>
            <div v-if="prediction" class="mt-4 prediction-result">
              <h5>예측 결과</h5>
              <div class="prediction-bar">
                <div
                  :class="{
                    medium: prediction.win_rate === '낮음',
                  }"
                  class="prediction-item"
                >
                  보통
                </div>
                <div
                  :class="{
                    medium: prediction.win_rate === '보통',
                  }"
                  class="prediction-item"
                >
                  높음
                </div>
                <div
                  :class="{
                    medium: prediction.win_rate === '높음',
                  }"
                  class="prediction-item"
                >
                  매우 높음
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card_list shadow-sm mb-5">
      <div class="card-header p-2">
        <h4>무주택기간</h4>
      </div>
      <div class="card-body p-3">
        <div class="form-group d-flex flex-wrap">
          <div class="form-check mx-2 my-1" v-for="(option, index) in housingPeriodOptions" :key="index">
            <input
              class="form-check-input"
              type="radio"
              :id="'housingPeriod' + index"
              name="housingPeriod"
              v-model="selectedHousingPeriod"
              :value="option.points"
            />
            <label class="form-check-label" :for="'housingPeriod' + index"
              >{{ option.label }} ({{ option.points }}점)</label
            >
          </div>
        </div>
      </div>
    </div>

    <div class="card_list shadow-sm mb-5">
      <div class="card-header p-2">
        <h4>부양가족수</h4>
      </div>
      <div class="card-body p-3">
        <div class="form-group d-flex flex-wrap">
          <div class="form-check mx-2 my-1" v-for="(option, index) in familyMembersOptions" :key="index">
            <input
              class="form-check-input"
              type="radio"
              :id="'familyMembers' + index"
              name="familyMembers"
              v-model="selectedFamilyMembers"
              :value="option.points"
            />
            <label class="form-check-label" :for="'familyMembers' + index"
              >{{ option.label }} ({{ option.points }}점)</label
            >
          </div>
        </div>
      </div>
    </div>

    <div class="card_list shadow-sm mb-5">
      <div class="card-header p-2">
        <h4>청약통장 가입기간</h4>
      </div>
      <div class="card-body p-3">
        <div class="form-group">
          <div class="row">
            <div class="col">
              <label>가입자 생일</label>
              <select class="form-control" v-model="selectedBirthYear" @change="calculateAge">
                <option disabled value="">년</option>
                <option v-for="year in years" :key="year" :value="year">{{ year }}년</option>
              </select>
            </div>
            <div class="col">
              <label>&nbsp;</label>
              <select class="form-control" v-model="selectedBirthMonth" @change="calculateAge">
                <option disabled value="">월</option>
                <option v-for="month in months" :key="month" :value="month">{{ month }}월</option>
              </select>
            </div>
            <div class="col">
              <label>&nbsp;</label>
              <select class="form-control" v-model="selectedBirthDay" @change="calculateAge">
                <option disabled value="">일</option>
                <option v-for="day in days" :key="day" :value="day">{{ day }}일</option>
              </select>
            </div>
          </div>

          <div class="row mt-3">
            <div class="col">
              <label>청약통장 가입일</label>
              <select class="form-control" v-model="selectedJoinYear">
                <option disabled value="">년</option>
                <option v-for="year in years" :key="year" :value="year">{{ year }}년</option>
              </select>
            </div>
            <div class="col">
              <label>&nbsp;</label>
              <select class="form-control" v-model="selectedJoinMonth">
                <option disabled value="">월</option>
                <option v-for="month in months" :key="month" :value="month">{{ month }}월</option>
              </select>
            </div>
            <div class="col">
              <label>&nbsp;</label>
              <select class="form-control" v-model="selectedJoinDay">
                <option disabled value="">일</option>
                <option v-for="day in days" :key="day" :value="day">{{ day }}일</option>
              </select>
            </div>
          </div>
        </div>
        <div class="text-center">
          <button class="btn mt-4 btn-custom" @click="calculatePoints">계산하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getPrediction } from '@/api/map.js';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const route = useRoute();
const eventDetail = ref({
  no: route.query.no,
  title: route.query.title,
  address: route.query.address,
  url: route.query.url,
  suply: route.query.suply,
  area: route.query.area,
  presnatn: route.query.presnatn,
  entrps: route.query.entrps,
});

// 사용자가 로그인했을 때 거주 지역을 설정
onMounted(() => {
  console.log(userInfo);
  if (userInfo.value) {
    formData.value.reside_area = userInfo.value.userAddress.slice(0, 5); // 사용자 거주 지역 설정
  }
});

const selectedBirthYear = ref('');
const selectedBirthMonth = ref('');
const selectedBirthDay = ref('');

// 사용자가 로그인했을 때 거주 지역을 설정
onMounted(() => {
  console.log(userInfo);
  if (userInfo.value) {
    formData.value.reside_area = userInfo.value.userAddress.slice(0, 5); // 사용자 거주 지역 설정
  }
});

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const housingPeriodOptions = ref([
  { label: '30세 미만 미혼 무주택자', points: 0 },
  { label: '1년 미만', points: 2 },
  { label: '1년 이상 ~ 2년 미만', points: 4 },
  { label: '2년 이상 ~ 3년 미만', points: 6 },
  { label: '3년 이상 ~ 4년 미만', points: 8 },
  { label: '4년 이상 ~ 5년 미만', points: 10 },
  { label: '5년 이상 ~ 6년 미만', points: 12 },
  { label: '6년 이상 ~ 7년 미만', points: 14 },
  { label: '7년 이상 ~ 8년 미만', points: 16 },
  { label: '8년 이상 ~ 9년 미만', points: 18 },
  { label: '9년 이상 ~ 10년 미만', points: 20 },
  { label: '10년 이상 ~ 11년 미만', points: 22 },
  { label: '11년 이상 ~ 12년 미만', points: 24 },
  { label: '12년 이상 ~ 13년 미만', points: 26 },
  { label: '13년 이상 ~ 14년 미만', points: 28 },
  { label: '14년 이상', points: 32 },
]);

const familyMembersOptions = ref([
  { label: '0명', points: 5 },
  { label: '1명', points: 10 },
  { label: '2명', points: 15 },
  { label: '3명', points: 20 },
  { label: '4명', points: 25 },
  { label: '5명', points: 30 },
  { label: '6명 이상', points: 35 },
]);

const years = ref([]);
const months = ref([]);
const days = ref([]);

for (let i = new Date().getFullYear(); i >= 1900; i--) {
  years.value.push(i);
}

for (let i = 1; i <= 12; i++) {
  months.value.push(i);
}

for (let i = 1; i <= 31; i++) {
  days.value.push(i);
}

const selectedHousingPeriod = ref(null);
const selectedFamilyMembers = ref(null);
const selectedJoinYear = ref('');
const selectedJoinMonth = ref('');
const selectedJoinDay = ref('');

const calculatePoints = () => {
  const totalPoints = selectedHousingPeriod.value + selectedFamilyMembers.value;
  formData.value.score = totalPoints; // 계산된 점수를 예측 폼에 설정
  alert(`가점 점수: ${totalPoints}`);
  window.scrollTo({ top: 0, behavior: 'smooth' }); //
};

// 나이 계산 함수
const calculateAge = () => {
  if (selectedBirthYear.value && selectedBirthMonth.value && selectedBirthDay.value) {
    const today = new Date();
    const birthDate = new Date(selectedBirthYear.value, selectedBirthMonth.value - 1, selectedBirthDay.value);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDifference = today.getMonth() - birthDate.getMonth();
    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    formData.value.age = age;
  }
};

// 청약 당첨 예측
const formData = ref({
  age: null,
  reside_area: '',
  score: null,
});

const prediction = ref(null);

const fetchPrediction = () => {
  console.log(formData.value.age);
  if (eventDetail.value.area === formData.value.reside_area) {
    formData.value.reside_area = '해당지역';
  } else {
    formData.value.reside_area = '기타지역';
  }
  getPrediction(
    formData.value,
    ({ data }) => {
      if (data) {
        prediction.value = data;
      } else {
        prediction.value = null;
      }
    },
    (error) => {
      console.error('Error predicting:', error);
      prediction.value = null;
    }
  );
};
</script>

<style scoped>
.container {
  background-color: #f8f9fa; /* 연한 회색 배경색 */
  padding: 20px;
  border-radius: 10px;
}

.card {
  border: 1px solid #dee2e6; /* 연한 회색 테두리 */
  border-radius: 10px;
}

.card_list {
  border: 1px solid #dee2e6; /* 연한 회색 테두리 */
  border-radius: 10px;
}

.card-header {
  background-color: #5ab2ff; /* 이전에 사용한 배경색 */
  color: white;
  font-weight: bold;
  text-align: center;
  align-items: center;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.table {
  margin-top: 20px;
}

.btn-primary {
  background-color: #8cc0de;
  border-color: #8cc0de;
  background-color: #8cc0de;
  border-color: #8cc0de;
}

.btn-primary:hover {
  background-color: #5bbaf1;
  border-color: #5bbaf1;
  background-color: #5bbaf1;
  border-color: #5bbaf1;
}

.btn-secondary {
  background-color: #555;
  border-color: #6c757d;
}

.btn-secondary:hover {
  background-color: #0f0f0f;
  background-color: #0f0f0f;
  border-color: #545b62;
}

.btn-success {
  background-color: #28a745;
  border-color: #28a745;
}

.btn-success:hover {
  background-color: #218838;
  border-color: #1e7e34;
}

.btn-custom {
  font-weight: bold;
  color: white;
  background-color: #8cc0de;
  border-color: #8cc0de;
  background-color: #8cc0de;
  border-color: #8cc0de;
}

.btn-custom:hover {
  background-color: #5bbaf1;
  border-color: #5bbaf1;
  background-color: #5bbaf1;
  border-color: #5bbaf1;
}

.btn-block {
  width: 100%;
}

.prediction-result {
  background-color: #e9ecef;
  padding: 10px;
  border-radius: 10px;
}

.prediction-bar {
  display: flex;
  border: 1px solid #dee2e6;
  border-radius: 10px;
  overflow: hidden;
}

.prediction-item {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 10px;
  font-weight: bold;
  color: #000; /* 기본 텍스트 색상 */
}

.prediction-item.low {
  background-color: #ffc107; /* 기본 배경색 */
}

.prediction-item.medium {
  background-color: #ffc107; /* 기본 배경색 */
}
.prediction-item.high {
  background-color: #ffc107; /* 기본 배경색 */
}

.prediction-item.medium.active {
  background-color: #ffc107;
  color: #fff;
}
.mt-4 {
  margin-top: 1.5rem;
}

.mb-4 {
  margin-bottom: 1.5rem;
}

.event-image {
  width: 100%;
  height: auto;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.form-check {
  flex: 1 1 calc(33% - 1rem); /* 3개씩 나열, 마진을 고려한 크기 조절 */
  margin: 0.5rem;
}

.card-body {
  padding-bottom: 1rem; /* 카드 바디의 여백 조정 */
}

.text-center {
  text-align: center;
}
</style>
