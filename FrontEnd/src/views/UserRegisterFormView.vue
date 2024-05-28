<script setup>
import axios from 'axios';
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { duplicatedCheck, mailValidation } from '@/api/user.js';
import { httpStatusCode } from '@/util/http-status.js';

const store = useUserStore();
const { storeRegistUser } = store;
const user = ref({
  userId: '',
  userPassword: '',
  userEmail: '',
  userPhone: '',
  userAddress: '',
  userAddressDetail: '',
});
const isNotDuplicated = ref(true);
const isEstate = ref(false);
const confirmPassword = ref('');
const passwordMatchMessage = ref('');

const phonePart1 = ref('010');
const phonePart2 = ref('');
const phonePart3 = ref('');

const validation = ref(false);
const isEmailSent = ref(false);
const eamilAuthNum = ref('');
const enterAuthNum = ref('');
const emailValiMessage = ref('');

const regist = async () => {
  if (!isNotDuplicated.value) {
    alert('중복된 아이디입니다. 다른 아이디를 사용해주세요.');
    return;
  }
  if (!validation.value) {
    alert('비밀번호를 일치시켜주세요.');
    return;
  }
  if (!isEmailSent.value || eamilAuthNum.value !== enterAuthNum.value) {
    alert('이메일 인증을 완료해주세요.');
    return;
  }
  user.value.userRole = isEstate.value ? 'estate' : 'user';
  user.value.userPhone = `${phonePart1.value}-${phonePart2.value}-${phonePart3.value}`;
  user.value.userAddress = `${user.value.userAddress} ${user.value.userAddressDetail}`;
  user.value.userAddressDetail = ''; // 상세주소 초기화
  storeRegistUser(user.value);
};

const sendVerificationEmail = async () => {
  await mailValidation(
    user.value.userEmail,
    (response) => {
      isEmailSent.value = true;
      eamilAuthNum.value = response.data.authNumber;
      alert('인증번호가 이메일로 전송되었습니다.');
      console.log('authNumber = ', eamilAuthNum.value);
    },
    (error) => {
      alert('이메일 전송 중 오류가 발생하였습니다.');
    }
  );
};

const verifyEmailCode = () => {
  if (eamilAuthNum.value === enterAuthNum.value) {
    emailValiMessage.value = '이메일 인증이 완료되었습니다.';
  } else {
    emailValiMessage.value = '인증번호가 일치하지 않습니다.';
  }
};

watch([user.value.userPassword, confirmPassword], () => {
  if (confirmPassword.value === '') {
    passwordMatchMessage.value = '';
  } else if (user.value.userPassword === confirmPassword.value) {
    passwordMatchMessage.value = '비밀번호가 일치합니다.';
    validation.value = true;
  } else {
    passwordMatchMessage.value = '비밀번호가 일치하지 않습니다.';
    validation.value = false;
  }
});

watch(
  () => user.value.userId,
  (newValue, oldValue) => {
    idCheckMessage.value = '';
  }
);

const idCheckMessage = ref('');
const checkUserId = async () => {
  duplicatedCheck(
    user.value.userId,
    (response) => {
      if (response.status === httpStatusCode.OK) {
        idCheckMessage.value = '아이디를 사용할 수 있습니다.';
        isNotDuplicated.value = true;
      } else if (response.status === httpStatusCode.ACCEPTED) {
        idCheckMessage.value = '아이디가 이미 사용 중입니다.';
        isNotDuplicated.value = false;
      }
    },
    (error) => {
      idCheckMessage.value = '아이디 중복조회 중 오류가 발생하였습니다.';
    }
  );
};

const validateNumberInput = (event) => {
  const value = event.target.value;
  if (!/^\d*$/.test(value)) {
    event.target.value = value.replace(/\D/g, '');
  }
};

const search = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      console.log(data);
      user.value.userAddress = data.address;
    },
  }).open();
};
</script>

<template>
  <div class="register-container">
    <form @submit.prevent="regist" class="register-form">
      <h2>회원가입</h2>
      <div class="form-group mt-3 d-flex align-items-center">
        <input type="text" placeholder="아이디" v-model="user.userId" class="form-control flex-grow-1" />
        <button type="button" class="btn btn-secondary ml-2 flex-shrink-0" @click="checkUserId">중복확인</button>
      </div>
      <div v-if="idCheckMessage" class="id-check-message mt-2">{{ idCheckMessage }}</div>
      <div class="form-group mt-3">
        <input type="password" placeholder="비밀번호" v-model="user.userPassword" class="form-control" />
      </div>
      <div class="form-group mt-3">
        <input type="password" placeholder="비밀번호 확인" v-model="confirmPassword" class="form-control" />
      </div>
      <div
        class="password-message"
        :class="{
          'text-success': passwordMatchMessage === '비밀번호가 일치합니다.',
          'text-danger': passwordMatchMessage === '비밀번호가 일치하지 않습니다.',
        }"
      >
        {{ passwordMatchMessage }}
      </div>
      <div class="form-group mt-3">
        <input type="email" placeholder="이메일" v-model="user.userEmail" class="form-control" />
        <button type="button" class="btn btn-primary ml-2 flex-shrink-0" @click="sendVerificationEmail">
          인증번호 발송
        </button>
      </div>
      <div class="form-group mt-3 d-flex align-items-center">
        <input type="text" placeholder="인증번호 입력" v-model="enterAuthNum" class="form-control flex-grow-1" />
        <button type="button" class="btn btn-secondary ml-2 flex-shrink-0" @click="verifyEmailCode">
          인증번호 확인
        </button>
      </div>
      <div v-if="emailValiMessage" class="email-verification-message mt-2">{{ emailValiMessage }}</div>
      <div class="form-group mt-3 d-flex align-items-center">
        <select v-model="phonePart1" class="form-control phone-select">
          <option value="010">010</option>
          <option value="011">011</option>
        </select>
        <input
          type="text"
          placeholder="1234"
          v-model="phonePart2"
          class="form-control phone-input"
          maxlength="4"
          @input="validateNumberInput"
        />
        <input
          type="text"
          placeholder="5678"
          v-model="phonePart3"
          class="form-control phone-input"
          maxlength="4"
          @input="validateNumberInput"
        />
      </div>
      <div class="form-group mt-3 d-flex align-items-center">
        <input type="text" placeholder="주소" v-model="user.userAddress" class="form-control flex-grow-1" />
        <button type="button" class="btn btn-primary ml-2 flex-shrink-0" @click="search">우편번호 찾기</button>
      </div>
      <div class="form-group mt-3">
        <input type="text" placeholder="상세주소" v-model="user.userAddressDetail" class="form-control" />
      </div>
      <div class="form-check mt-3">
        <input type="checkbox" class="form-check-input" id="isEstate" v-model="isEstate" />
        <label class="form-check-label" for="isEstate">부동산 중개인입니다</label>
      </div>
      <button type="submit" class="btn btn-primary register-btn mt-4">회원가입</button>
    </form>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.register-form {
  background-color: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 600px;
  text-align: center;
}

.register-form h2 {
  margin-bottom: 1rem;
  color: #5bbaf1;
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
}

.form-control {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  flex-grow: 1;
}

.form-control::placeholder {
  color: #ccc;
}

.phone-select,
.phone-input {
  flex-basis: 30%;
  max-width: 30%;
  margin-right: 0.5rem; /* 간격 조정 */
}

.phone-input:last-of-type {
  margin-right: 0; /* 마지막 input의 마진 제거 */
}

.password-message {
  font-size: 0.9rem;
  margin-top: -10px;
  margin-bottom: 10px;
}

.text-success {
  color: green;
}

.text-danger {
  color: red;
}

.form-check {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.form-check-input {
  margin-right: 0.5rem;
}

.form-check-label {
  color: #666;
}

.btn {
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  color: #fff;
  font-weight: bold;
  cursor: pointer;
}

.btn-primary {
  background-color: #8cc0de;
}

.btn-primary:hover {
  background-color: #5bbaf1;
}

.btn-secondary {
  background-color: #8cc0de;
  margin-left: 10px;
}

.btn-secondary:hover {
  background-color: #5bbaf1;
  margin-left: 10px;
}

.register-btn {
  margin-top: 1rem;
}

.id-check-message {
  font-size: 0.9rem;
  color: #555;
}

.email-verification-message {
  font-size: 0.9rem;
  color: #555;
}

.d-flex {
  display: flex;
}

.ml-2 {
  margin-left: 0.5rem;
}

.flex-grow-1 {
  flex-grow: 1;
}

.flex-shrink-0 {
  flex-shrink: 0;
}
</style>
