<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';
const store = useUserStore();
const router = useRouter();
const { userLogin, storeGetUserInfo, storeRegenerateToken, storeLogOut, storeRegistToken } = store;
const { userInfo, isLogin } = storeToRefs(store);
const data = ref({});
const user = ref({
  userId: '',
  userPassword: '',
});
const login = async () => {
  await userLogin(user.value);
  let token = sessionStorage.getItem('accessToken');
  if (isLogin.value) {
    await storeRegistToken();
    await storeGetUserInfo(token);
    data.value = userInfo;
  }
};
const regenerateToken = () => {
  storeRegenerateToken();
};
const logout = () => {
  storeLogOut();
};
const moveResetPassword = () => {
  router.push({ name: 'reset' });
};
</script>

<template>
  <div class="login-container">
    <form @submit.prevent="login" class="login-form">
      <h2>로그인</h2>
      <div class="form-group mt-5">
        <input type="text" placeholder="Email" v-model="user.userId" class="form-control" />
      </div>
      <div class="form-group mt-3">
        <input type="password" placeholder="Password" v-model="user.userPassword" class="form-control" />
      </div>
      <div class="form-group form-check">
        <input type="checkbox" class="form-check-input" id="rememberMe" />
        <label class="form-check-label" for="rememberMe">아이디 저장하기</label>
      </div>
      <button type="submit" class="btn btn-primary login-btn">Login</button>
      <div class="login-options">
        <div>
          <input type="checkbox" id="rememberMe" />
          <label for="rememberMe">로그인 상태 유지</label>
        </div>
        <a href="#" class="forgot-password" @click="moveResetPassword">비밀번호 찾기</a>
      </div>
    </form>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 94vh;
}
.login-btn {
  margin-top: 100px;
}
.login-form {
  background-color: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 600px; /* 너비를 400px로 확장 */
  height: 500px; /* 높이를 500px로 확장 */
  text-align: center;
}

.login-form h2 {
  margin-bottom: 1rem;
  color: #5bbaf1;
}

.form-group {
  margin-bottom: 1rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-control::placeholder {
  color: #ccc;
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
  width: 100%;
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
  background-color: #888;
  margin-top: 0.5rem;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
}

.login-options label {
  font-size: 0.9rem;
  color: #666;
}

.login-options .forgot-password {
  font-size: 0.9rem;
  color: #888;
  text-decoration: none;
  font-weight: bold;
}

.login-options .forgot-password:hover {
  text-decoration: underline;
}
</style>
