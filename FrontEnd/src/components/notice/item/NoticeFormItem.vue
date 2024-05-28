<template>
  <div class="notice-form p-5">
    <form @submit.prevent="onSubmit" class="form-container">
      <div class="form-group">
        <label for="title">제목</label>
        <input type="text" id="title" v-model="notice.title" required />
        <div class="error-msg" v-if="titleErrMsg">{{ titleErrMsg }}</div>
      </div>
      <div class="form-group">
        <label for="content">내용</label>
        <textarea id="content" v-model="notice.content" required></textarea>
        <div class="error-msg" v-if="contentErrMsg">{{ contentErrMsg }}</div>
      </div>
      <div class="form-group">
        <label for="writer">작성자</label>
        <input type="text" id="writer" v-model="writer" readonly />
      </div>
      <div class="button-group">
        <button @click="goBack" type="button" class="back-button">돌아가기</button>
        <button type="submit" class="submit-button">{{ type === 'modify' ? '수정' : '등록' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { insertNotice, updateNotice, getNoticeById } from '@/api/notice';

const router = useRouter();
const route = useRoute();
const props = defineProps({ type: String });

const notice = ref({
  id: 0,
  title: '',
  content: '',
  user_id: '',
});

const titleErrMsg = ref('');
const contentErrMsg = ref('');

const writer = computed(() => {
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
  return userInfo ? userInfo.userId : '';
});

// 사용자 정보 가져오기
const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
if (userInfo && userInfo.userId) {
  notice.value.user_id = userInfo.userId;
}
const goBack = () => {
  router.push({ name: 'NoticeView' });
};

watch(
  () => notice.value.title,
  (value) => {
    let len = value.length;
    if (len === 0 || len > 100) {
      titleErrMsg.value = '제목을 확인해 주세요!!!';
    } else {
      titleErrMsg.value = '';
    }
  },
  { immediate: true }
);

watch(
  () => notice.value.content,
  (value) => {
    let len = value.length;
    if (len === 0 || len > 1000) {
      contentErrMsg.value = '내용을 확인해 주세요!!!';
    } else {
      contentErrMsg.value = '';
    }
  },
  { immediate: true }
);

onMounted(() => {
  if (props.type === 'modify') {
    const noticeId = route.params.id;
    fetchNotice(noticeId);
  }
});

function fetchNotice(noticeId) {
  getNoticeById(
    noticeId,
    (response) => {
      notice.value = response.data;
    },
    (error) => {
      console.error(error);
    }
  );
}

function onSubmit() {
  if (titleErrMsg.value) {
    alert(titleErrMsg.value);
  } else if (contentErrMsg.value) {
    alert(contentErrMsg.value);
  } else {
    props.type === 'modify' ? modifyNotice() : writeNotice();
  }
}

function writeNotice() {
  console.log('공지사항 등록:', notice.value);
  insertNotice(
    notice.value,
    (response) => {
      let msg = '공지사항 등록 처리 시 문제가 발생했습니다.';
      if (response.status === 200) {
        msg = '공지사항 등록이 완료되었습니다.';
      }
      alert(msg);
      moveList();
    },
    (error) => {
      console.error(error);
    }
  );
}

function modifyNotice() {
  console.log('공지사항 수정:', notice.value);
  updateNotice(
    notice.value.id,
    notice.value,
    (response) => {
      let msg = '공지사항 수정 처리 시 문제가 발생했습니다.';
      if (response.status === 200) {
        msg = '공지사항 수정이 완료되었습니다.';
      }
      alert(msg);
      moveList();
    },
    (error) => {
      console.error(error);
    }
  );
}

function moveList() {
  router.push({ name: 'NoticeView' });
}
</script>

<style scoped>
.notice-form {
  margin: 20px auto;
  padding: 20px;
  max-width: 600px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-container {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
  color: #555;
}

input[type='text'],
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.button-group {
  display: flex;
  justify-content: space-between;
}
.back-button {
  background-color: #555;
}

.back-button:hover {
  background-color: #0f0f0f;
}

button {
  padding: 10px 20px;
  background-color: #8cc0de;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #5bbaf1;
}

.error-msg {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}
</style>
