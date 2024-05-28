<template>
  <div class="notice-detail" v-if="notice">
    <div class="notice-header"></div>
    <div class="notice-title">
      <h2>{{ notice.title }}</h2>
    </div>
    <div class="notice-meta">
      <span>작성자: {{ notice.user_id }}</span>
      <span>작성일: {{ formatDate(notice.created_at) }}</span>
    </div>
    <div class="notice-content pb-5">
      <p>{{ notice.content }}</p>
    </div>
    <div class="notice-actions mt-5">
      <button @click="goBack" class="back-button">돌아가기</button>
      <div v-if="isAuthor">
        <button @click="editNotice" class="edit-button">수정</button>
        <button @click="deleteNoticeItem" class="delete-button">삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getNoticeById, deleteNotice } from '@/api/notice';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';

const notice = ref(null);
const route = useRoute();
const router = useRouter();
const noticeId = route.params.id;

const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);
const userId = computed(() => userInfo.value && userInfo.value.userId === 'admin');

const formatDate = (dateString) => {
  return dateString.slice(0, 10);
};

const fetchNotice = () => {
  getNoticeById(
    noticeId,
    (response) => {
      notice.value = response.data;
    },
    (error) => {
      console.error('Failed to fetch notice:', error);
    }
  );
};

const editNotice = () => {
  router.push({ name: 'NoticeModify', params: { id: noticeId } });
};

const deleteNoticeItem = () => {
  deleteNotice(
    noticeId,
    () => {
      alert('공지사항이 삭제되었습니다.');
      router.push({ name: 'NoticeView' });
    },
    (error) => {
      console.error('Failed to delete notice:', error);
    }
  );
};

const isAuthor = computed(() => {
  return notice.value.user_id === userId.value;
});

const goBack = () => {
  router.push({ name: 'NoticeView' });
};

onMounted(() => {
  fetchNotice();
});
</script>

<style scoped>
.notice-detail {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.notice-header {
  text-align: center;
  margin-bottom: 20px;
}

.notice-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.divider {
  margin: 20px 0;
  border: none;
  border-top: 1px solid #ccc;
}

.notice-title {
  text-align: center;
  margin-bottom: 20px;
}

.notice-title h2 {
  margin: 0;
  font-size: 22px;
  color: #333;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.notice-content {
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}

.notice-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  font-weight: bold;
}

.back-button {
  background-color: #555;
}

.back-button:hover {
  background-color: #0f0f0f;
}
.edit-button {
  background-color: #8cc0de;
}

.edit-button:hover {
  background-color: #5bbaf1;
}

.delete-button {
  background-color: #ff9494;
}

.delete-button:hover {
  background-color: #fd5c5c;
}
</style>
