<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';
import { mailValidation, resetPassword } from '@/api/user.js';
import { httpStatusCode } from '@/util/http-status.js';
const store = useUserStore();
const router = useRouter();

const user = ref({
  userId: '',
  userEmail: '',
  verificationCode: '',
  newPassword: '',
});

const validation = ref(false);
const isEmailSent = ref(false);
const eamilAuthNum = ref('');
const enterAuthNum = ref('');
const emailValiMessage = ref('');

const confirmPassword = ref('');
const passwordMatchMessage = ref('');

watch([user.value.newPassword, confirmPassword], () => {
  if (confirmPassword.value === '') {
    passwordMatchMessage.value = '';
  } else if (user.value.newPassword === confirmPassword.value) {
    passwordMatchMessage.value = '비밀번호가 일치합니다.';
    validation.value = true;
  } else {
    passwordMatchMessage.value = '비밀번호가 일치하지 않습니다.';
    validation.value = false;
  }
});

const sendCode = async () => {
  await mailValidation(
    user.value.userEmail,
    (response) => {
      alert('인증번호가 이메일로 전송되었습니다.');
      eamilAuthNum.value = response.data.authNumber;
      isEmailSent.value = true;
    },
    (error) => {
      alert('이메일 전송 중 오류가 발생하였습니다.');
    }
  );
};

const verify = () => {
  if (eamilAuthNum.value === enterAuthNum.value) {
    emailValiMessage.value = '이메일 인증이 완료되었습니다.';
  } else {
    emailValiMessage.value = '인증번호가 일치하지 않습니다.';
  }
};

const reset = async () => {
  if (!isEmailSent.value || eamilAuthNum.value !== enterAuthNum.value) {
    alert('이메일 인증을 완료해주세요.');
    return;
  }
  let sendObject = {
    userId: user.value.userId,
    userPassword: user.value.newPassword,
  };
  resetPassword(
    sendObject,
    (response) => {
      if (response.status === httpStatusCode.ACCEPTED) {
        alert(`${response.data.message}`);
        return;
      } else if (response.status === httpStatusCode.OK) {
        alert('비밀번호 변경에 성공했습니다.');
        router.push({ name: 'login' });
      }
    },
    (error) => {
      alert('비밀번호 변경 중 에러가 발생했습니다.');
    }
  );
};
</script>

<template>
  <div class="container password-reset-container">
    <form @submit.prevent="reset" class="password-reset-form">
      <h2>비밀번호 찾기</h2>
      <div class="form-group mt-5">
        <input type="text" placeholder="아이디" v-model="user.userId" class="form-control" />
      </div>
      <div class="form-group mt-3 d-flex">
        <input type="email" placeholder="이메일" v-model="user.userEmail" class="form-control flex-grow-1" />
        <button type="button" class="btn btn-secondary ml-2" @click="sendCode">인증번호 발송</button>
      </div>
      <div class="form-group mt-3 d-flex">
        <input type="text" placeholder="인증번호" v-model="enterAuthNum" class="form-control flex-grow-1" />
        <button type="button" class="btn btn-secondary ml-2" @click="verify">인증번호 확인</button>
      </div>
      <div v-if="emailValiMessage" class="email-verification-message mt-2">{{ emailValiMessage }}</div>
      <div class="form-group mt-3">
        <input type="password" placeholder="새 비밀번호" v-model="user.newPassword" class="form-control" />
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
      <button type="submit" class="btn btn-primary password-reset-btn">비밀번호 재설정</button>
    </form>
  </div>
</template>

<style scoped>
.password-reset-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 94vh;
}
.password-reset-form {
  background-color: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 600px;
  height: auto;
  text-align: center;
}

.password-reset-form h2 {
  margin-bottom: 1rem;
  color: #5bbaf1;
}

.form-group {
  margin-bottom: 1rem;
}

.d-flex {
  display: flex;
  align-items: center;
}

.flex-grow-1 {
  flex-grow: 1;
}

.ml-2 {
  margin-left: 0.5rem;
}

.form-control {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-control::placeholder {
  color: #ccc;
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
}

.btn-secondary:hover {
  background-color: #5bbaf1;
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
</style>
