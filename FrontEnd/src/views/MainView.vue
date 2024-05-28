<template>
  <div class="container">
    <div class="banner">
      <img src="/public/img/main_test.jpg" style="width: 1160px" height="100%" />
    </div>

    <div class="main-content">
      <div class="section calendar-section" @click="navigateTo('SubscriptionView')">
        <h2><strong>청약캘린더</strong></h2>
        <p>금일 청약일정을 놓치지말고 확인하세요!</p>
        <img src="/public/img/calendar.png" class="icon" />
      </div>
      <div class="section announcement-section" @click="navigateTo('HouseView')">
        <h2><strong>매물</strong></h2>
        <p>최근 업데이트된 매물을 확인하세요!</p>
        <img src="/public/img/apt.png" class="icon" />
      </div>
      <div class="section recent-announcements" @click="navigateTo('NoticeView')">
        <h2><strong>공지사항</strong></h2>
        <P>관리자가 공지하는 정보를 확인하세요!</P>
        <img src="/public/img/loud.png" class="icon" />
      </div>
    </div>
    <h2 class="news-list-title"><strong>뉴스 리스트</strong></h2>
    <div v-if="loading" class="loading">
      <img src="/public/img/loading1.gif" alt="Loading..." />
    </div>
    <div v-else class="card-slider">
      <button
        v-if="currentPage > 0"
        @click="prevSlide"
        class="nav-button left-button"
        @mouseover="hoverButton = 'left'"
        @mouseleave="hoverButton = null"
        :class="{ hover: hoverButton === 'left' }"
      >
        <font-awesome-icon :icon="['fas', 'arrow-left']" />
      </button>
      <div class="cards-container">
        <div class="cards">
          <div v-for="(article, index) in visibleArticles" :key="index" class="card">
            <img :src="article.images.length ? article.images[0] : '/public/img/defaultApt.jpg'" alt="Article Image" />
            <h3 v-html="article.title"></h3>
            <a
              :href="article.link"
              target="_blank"
              class="see-more-button"
              @mouseover="hover[index] = true"
              @mouseleave="hover[index] = false"
              :class="{ hover: hover[index] }"
              >기사 보기</a
            >
          </div>
        </div>
      </div>
      <button
        v-if="currentPage < totalPages - 1"
        @click="nextSlide_btn"
        class="nav-button right-button"
        @mouseover="hoverButton = 'right'"
        @mouseleave="hoverButton = null"
        :class="{ hover: hoverButton === 'right' }"
      >
        <font-awesome-icon :icon="['fas', 'arrow-right']" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getNewsList } from '@/api/main';
import { getNoticeList } from '@/api/notice';

const router = useRouter();
const articles = ref([]);
const latestAnnouncements = ref([]);

const currentPage = ref(0);
const articlesPerPage = 3;

const totalPages = computed(() => Math.ceil(articles.value.length / articlesPerPage));

const visibleArticles = computed(() => {
  const start = currentPage.value * articlesPerPage;
  const end = start + articlesPerPage;
  return articles.value.slice(start, end);
});

const nextSlide_btn = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
  }
};

const prevSlide = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
};

const loading = ref(true);

const fetchNews = () => {
  getNewsList(
    (response) => {
      articles.value = response.data[0].items.map((article) => ({
        ...article,
        title: article.title.replace(/<b>/g, '<strong>').replace(/<\/b>/g, '</strong>'),
        description: article.description.replace(/<b>/g, '<strong>').replace(/<\/b>/g, '</strong>'),
        images: article.images || [],
      }));
      loading.value = false;
    },
    (error) => {
      console.error(error);
      loading.value = false;
    }
  );
};

const fetchLatestAnnouncements = () => {
  const params = { limit: 5, sort: 'desc' };
  getNoticeList(
    params,
    (response) => {
      latestAnnouncements.value = response.data.slice(0, 3);
    },
    (error) => {
      console.error(error);
    }
  );
};

const slides = ['/public/img/ad1.jpg', '/public/img/ad2.jpg', '/public/img/ad3.jpg'];
const currentSlide = ref(0);

const goToSlide = (index) => {
  currentSlide.value = index;
};

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.length;
};

const navigateTo = (view) => {
  router.push({ name: view });
};

const hover = ref({});
const hoverButton = ref(null);

onMounted(() => {
  fetchNews();
  fetchLatestAnnouncements();
  setInterval(nextSlide, 5000);
});
</script>

<style scoped>
.container {
  position: relative;
}

.icon {
  width: 160px; /* 아이콘 크기 조정 */
  height: auto;
  display: block;
  margin: 0 auto;
}

.nav-button {
  background-color: #a0deff;
  border: none;
  color: white;
  padding: 10px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.nav-button.hover {
  transform: scale(1.1); /* 버튼 확대 효과 */
}

.left-button {
  position: absolute;
  left: -45px; /* 카드 슬라이더 가운데에 맞추기 위해 조정 */
  top: 80%;
  transform: translateY(-50%);
}

.right-button {
  position: absolute;
  right: -50px; /* 카드 슬라이더 가운데에 맞추기 위해 조정 */
  top: 80%;
  transform: translateY(-50%);
}

.cards-container {
  overflow: hidden;
}

.cards {
  display: flex;
  transition: transform 0.3s ease-in-out;
}

.card {
  min-width: 200px;
  margin: 0 10px;
}

.card img {
  width: 100%;
}

.card h3 {
  margin: 10px 0;
}

.see-more-button {
  display: block;
  margin-top: 10px;
  color: #000000;
  background-color: #a0deff;
  text-decoration: none;
  font-weight: bold;
  text-align: center;
  transition: transform 0.3s ease;
}

.see-more-button.hover {
  transform: scale(1.1); /* 버튼 확대 효과 */
}

.main-content {
  margin-bottom: 40px; /* 뉴스 리스트와의 구분을 위해 여백 추가 */
}

.news-list-title {
  border-top: 2px solid #a0deff; /* 뉴스 리스트와의 구분을 위한 상단 경계선 */
  padding-top: 20px;
  margin-top: 20px;
}
</style>
