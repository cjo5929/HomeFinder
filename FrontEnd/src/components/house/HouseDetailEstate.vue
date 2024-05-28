<template>
  <div class="estate-container">
    <h4>상담 가능한 매물</h4>
    <div class="estate-list">
      <div v-if="estateApartList.length === 0" class="no-estate-item">
        <p>상담 가능한</p>
        <p>매물이 없습니다.</p>
      </div>
      <div v-else v-for="(estate, index) in estateApartList" :key="index" class="estate-item">
        <img :src="`data:image/png;base64, ${estate.base64}`" alt="매물 이미지" class="estate-image" />
        <h3 class="estate-price">
          {{ estate.type === 'trading' ? '매매' : estate.type === 'monthly' ? '월세' : '전세' }}
        </h3>
        <h3 class="estate-price">{{ estate.price }}만원</h3>
        <p class="estate-size">{{ estate.area }}㎡</p>
        <p class="estate-building">{{ estate.floor }}층</p>
        <p class="estate-building">올린 업체 : {{ estate.user_id }}</p>
        <p class="estate-description">{{ estate.info }}</p>
        <div class="button-container">
          <button @click="insertFavorite(estate)" class="favorite-button">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
              <path
                d="M47.6 300.4L228.3 469.1c7.5 7 17.4 10.9 27.7 10.9s20.2-3.9 27.7-10.9L464.4 300.4c30.4-28.3 47.6-68 47.6-109.5v-5.8c0-69.9-50.5-129.5-119.4-141C347 36.5 300.6 51.4 268 84L256 96 244 84c-32.6-32.6-79-47.5-124.6-39.9C50.5 55.6 0 115.2 0 185.1v5.8c0 41.5 17.2 81.2 47.6 109.5z"
              />
            </svg>
          </button>
          <button @click="openChatting(estate.user_id, estate.item_no)" class="chat-button">상담하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/userStore';
import { addFavorite } from '@/api/userFavorite';
import { defineEmits } from 'vue';
import { httpStatusCode } from '@/util/http-status';

const store = useUserStore();
const { userInfo } = storeToRefs(store);
const list = defineProps({
  estateApartList: {
    type: Array,
    required: true,
  },
});
console.log('estate list =', list.estateApartList);
const insertFavorite = (estate) => {
  console.log(estate);
  addFavorite(
    estate,
    userInfo.value.userId,
    (response) => {
      if (response.status === httpStatusCode.OK) {
        alert('찜 등록에 성공했습니다.');
      } else if (response.status === httpStatusCode.ACCEPTED) {
        alert('이미 찜 목록에 있는 물품입니다.');
      }
    },
    (error) => {
      alert('찜 등록에 실패했습니다.');
    }
  );
};

const emit = defineEmits(['openChatting']);
const openChatting = (userId, itemNo) => {
  let sendObject = { userId: userId, itemNo: itemNo };
  emit('openChatting', sendObject);
};
</script>

<style scoped>
.estate-container {
  margin: 20px;
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.estate-list-wrapper {
  overflow-x: auto;
  white-space: nowrap;
  width: 100%;
}
.estate-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.estate-item,
.no-estate-item {
  min-width: 200px;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.2s;
}
.estate-item:hover {
  transform: scale(1.05);
}
.no-estate-item {
  font-size: 18px;
  color: #555;
}
.estate-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}
.estate-price {
  margin: 10px 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.estate-size,
.estate-building,
.estate-description {
  margin: 5px 0;
  font-size: 14px;
  color: #555;
}
.button-container {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
.favorite-button,
.chat-button {
  border: none;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}
.favorite-button {
  background-color: #ffcccb;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}
.favorite-button:hover {
  background-color: #ff9999;
}
.chat-button {
  background-color: #8cc0de;
  transition: background-color 0.3s;
}
.chat-button:hover {
  background-color: #5bbaf1;
}
.favorite-button svg {
  width: 20px;
  height: 20px;
  fill: #ff6666;
}
</style>
