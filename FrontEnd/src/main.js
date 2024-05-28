import { createApp } from 'vue';
import { createPinia } from 'pinia';
import { useKakao } from 'vue3-kakao-maps/@utils';
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const { VITE_KAKAO_MAP_SERVICE_KEY } = import.meta.env;

// fontawesome import
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { useUserStore } from "@/stores/userStore.js"; // Pinia store import
library.add(fas);

import App from './App.vue';
import router from './router';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';
import './assets/main.css';

// createApp을 통해 생성한Application 인스턴스의 component API 활용

const app = createApp(App);
app.component('font-awesome-icon', FontAwesomeIcon);

useKakao(VITE_KAKAO_MAP_SERVICE_KEY, ['clusterer', 'services', 'drawing']);

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate, {
  storage: sessionStorage
});
app.use(pinia);
app.use(router);

app.mount('#app');

// Pinia store 사용
const store = useUserStore();

// 이벤트 리스너를 추가하여 사용자가 브라우저를 종료할 때 핸들러를 실행
window.addEventListener('beforeunload', (event) => {
  // 여기서 isLogin과 userInfo 값을 null로 설정
  store.$patch({
    isLogin: false,
    userInfo: null
  });

  // 브라우저에 기본 행동을 무시하게 합니다.
  event.preventDefault();
  event.returnValue = '';
});