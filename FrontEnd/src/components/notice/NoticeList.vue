<template>
  <div class="notice-view p-5 mt-5">
    <h1>공지사항</h1>
    <div class="header">
      <div class="actions">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="검색어를 입력하세요"
          @keyup.enter="searchNotices"
          class="search-input"
        />
        <button @click="searchNotices" class="search-button">검색</button>
        <button v-if="isAdmin" @click="moveWrite" class="write-button">글 작성</button>
      </div>
    </div>
    <table class="notice-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>제목</th>
          <th>작성자</th>
          <th>작성일</th>
        </tr>
      </thead>
      <tbody>
        <NoticeListItem v-for="notice in notices" :key="notice.id" :notice="notice" />
      </tbody>
    </table>
    <VPagination :current-page="currentPage" :total-page="totalPage" @pageChange="handlePageChange" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { searchNotice, getNoticeCount } from '@/api/notice';
import NoticeListItem from '@/components/notice/item/NoticeListItem.vue';
import VPagination from '../common/VPagination.vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

// 컴퓨티드 속성을 사용하여 userInfo가 로드된 후에만 검사
const isAdmin = computed(() => userInfo.value && userInfo.value.userId === 'admin');

const notices = ref([]);
const currentPage = ref(1);
const pageSize = ref(10); // 한 페이지에 표시할 수를 10으로 설정
const totalPage = ref(0);
const searchQuery = ref('');

const router = useRouter();

const fetchNotices = () => {
  const params = { keyword: searchQuery.value, page: currentPage.value, size: pageSize.value };
  searchNotice(
    params,
    (response) => {
      notices.value = response.data.map((notice) => ({
        ...notice,
        title: notice.title,
        content: notice.content,
        writer: notice.user_id,
        created_at: notice.created_at.slice(0, 10),
      }));
      getNoticeCount(
        { keyword: searchQuery.value },
        (response) => {
          totalPage.value = Math.ceil(response.data / pageSize.value); // 총 페이지 수 계산
        },
        (error) => {
          console.error(error);
        }
      );
    },
    (error) => {
      console.error(error);
    }
  );
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchNotices();
};

const searchNotices = () => {
  currentPage.value = 1;
  fetchNotices();
};

const moveWrite = () => {
  router.push({ name: 'NoticeWrite' });
};

onMounted(() => {
  fetchNotices();
});
</script>

<style scoped>
.notice-view {
  padding: 20px;
  background-color: #f4f6f9;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
  color: #333;
}

.actions {
  display: flex;
  align-items: center;
}

.search-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  margin-right: 10px;
  flex-grow: 1;
  font-size: 16px;
}

.search-button,
.write-button {
  padding: 8px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  margin-left: 10px;
}

.search-button {
  background-color: #5bbaf1;
  color: white;
}

.search-button:hover {
  background-color: #8cc0de;
  color: white;
}

.write-button {
  background-color: #5bbaf1;
  color: white;
}

.write-button:hover {
  background-color: #8cc0de;
  color: white;
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
}

th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f7f7f7;
  color: #333;
  font-weight: bold;
}

tr {
  transition: background-color 0.2s ease;
}

tr:hover {
  background-color: #f1f1f1;
  cursor: pointer;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.page-item {
  margin: 0 5px;
}

.page-link {
  padding: 10px 15px;
  border-radius: 5px;
  border: 1px solid #ddd;
  color: #007bff;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.page-link:hover {
  background-color: #5bbaf1;
  color: white;
}

.page-item.active .page-link {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.page-item.disabled .page-link {
  cursor: not-allowed;
  color: #bbb;
}
</style>
