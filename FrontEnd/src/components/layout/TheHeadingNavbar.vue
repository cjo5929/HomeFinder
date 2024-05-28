<script setup>
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';
import logoSVG from '../common/SVGVUE/logoSVG.vue';

const store = useUserStore();
const { isLogin, userInfo } = storeToRefs(store);
const { storeLogOut } = store;

const logout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    storeLogOut();
  }
};
</script>

<template>
  <nav class="navbar navbar-expand-lg sticky-top">
    <div class="container-fluid">
      <router-link class="navbar-brand" :to="{ name: 'MainView' }">
        <logoSVG :size="100"></logoSVG>
      </router-link>
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarScroll"
        aria-controls="navbarScroll"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarScroll">
        <ul class="navbar-nav ms-auto my-2 my-lg-0 navbar-nav-scroll" style="--bs-scroll-height: 100px">
          <li class="nav-item">
            <router-link :to="{ name: 'HouseView' }" class="nav-link text-black fw-bold">매물 확인</router-link>
          </li>
          <li class="nav-item">
            <router-link :to="{ name: 'SubscriptionView' }" class="nav-link text-white fw-bold">청약 정보</router-link>
          </li>
          <li class="nav-item">
            <router-link :to="{ name: 'NoticeView' }" class="nav-link text-white fw-bold">공지사항</router-link>
          </li>
          <li class="nav-item" v-if="!isLogin">
            <router-link :to="{ name: 'login' }" class="nav-link text-white fw-bold">로그인</router-link>
          </li>
          <li class="nav-item" v-if="!isLogin">
            <router-link :to="{ name: 'regist' }" class="nav-link text-white fw-bold">회원가입</router-link>
          </li>
          <li class="nav-item" v-if="isLogin">
            <router-link :to="{ name: 'info' }" class="nav-link text-white fw-bold">마이페이지</router-link>
          </li>
          <li class="nav-item" v-if="isLogin">
            <a class="nav-link logout" @click.prevent="logout">로그아웃</a>
          </li>
          <li class="nav-item mt-2 nav-separator" v-if="isLogin"></li>
          <li class="nav-item mt-2" v-if="isLogin">
            <span class="navbar-text text-black fw-bold"> {{ userInfo.userId }}님 접속 중</span>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
:root {
  --nav-height: 70px; /* 네비게이션 바의 높이 설정 */
}

.navbar {
  height: var(--nav-height);
  padding: 0;
  background-color: #ffffff; /* 네비게이션 바 배경 색상 */
  background-color: #ffffff; /* 네비게이션 바 배경 색상 */
}

.nav-link {
  color: black !important; /* 네비게이션 링크 색상 */
  color: black !important; /* 네비게이션 링크 색상 */
  font-weight: bold; /* 네비게이션 링크 굵게 */
}

.nav-link:hover {
  background-color: #a0deff; /* 링크에 마우스 오버 시 배경 색상 */
  background-color: #a0deff; /* 링크에 마우스 오버 시 배경 색상 */
  border-radius: 5px; /* 둥근 모서리 */
}

.navbar-brand {
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
  font-weight: bold; /* 브랜드 이름 굵게 */
  color: white !important; /* 브랜드 이름 색상 */
}

.logo-img {
  height: 40px; /* 로고 이미지 높이 조정 */
  width: auto;
  margin-right: 10px; /* 로고와 텍스트 사이의 간격 조정 */
}

.navbar-toggler-icon {
  filter: invert(100%); /* 토글 아이콘 색상 반전 */
}

.logout:hover {
  cursor: pointer;
}

.nav-separator {
  border-left: 1px solid #ddd;
  height: 24px;
  margin-left: 15px;
  margin-right: 15px;
  display: flex;
  align-items: center;
}
</style>
