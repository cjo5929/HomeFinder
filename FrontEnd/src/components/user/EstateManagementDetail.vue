<template>
  <div class="container-xl main-div">
    <div class="row justify-content-center">
      <div class="col-12 text-center">
        <img :src="`data:image/png;base64, ${dataObj.base64}`" class="img-fluid mb-3 detail-image" />
        <h2>{{ dataObj.name }}</h2>
      </div>
    </div>
    <div class="row price-info">
      <div class="col-12">
        <div class="section-title">가격정보</div>
        <div class="card mb-3 price-card">
          <div class="card-body">
            <div class="row detail-item">
              <div class="col-4">
                <label>구분</label>
              </div>
              <div class="col-8">
                {{ type }}
              </div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>가격</label>
              </div>
              <div class="col-8">{{ dataObj.price }}원</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row detail-info">
      <div class="col-12">
        <div class="section-title">상세정보</div>
        <div class="card detail-card">
          <div class="card-body">
            <div class="row detail-item">
              <div class="col-4">
                <label>주소</label>
              </div>
              <div class="col-8">
                {{ dataObj.address }}
              </div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>방 개수</label>
              </div>
              <div class="col-8">{{ dataObj.room }} 개</div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>해당층</label>
              </div>
              <div class="col-8">{{ dataObj.floor }}층</div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>면적</label>
              </div>
              <div class="col-8">{{ dataObj.area }}m²</div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>욕실 수</label>
              </div>
              <div class="col-8">{{ dataObj.bath }}개</div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>난방종류</label>
              </div>
              <div class="col-8">
                {{ dataObj.heater }}
              </div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>현관 유형</label>
              </div>
              <div class="col-8">
                {{ dataObj.entrance }}
              </div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>입주가능일</label>
              </div>
              <div class="col-8">
                {{ dataObj.move_in }}
              </div>
            </div>
            <div class="row detail-item">
              <div class="col-4">
                <label>최초등록일</label>
              </div>
              <div class="col-8">
                {{ dataObj.regist_date }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row justify-content-center mt-4">
      <div class="col-3"></div>
      <button class="col btn btn-primary btn-lg mx-2" v-if="dataObj.user_id === userInfo.userId" @click="detailModify">
        수정
      </button>
      <div class="col-3"></div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const router = useRouter();
const store = useUserStore();
const { userInfo } = storeToRefs(store);
const { dataObj } = history.state;
console.log(dataObj);
const detailObject = ref({});
const type = ref('');
onMounted(() => {
  if (dataObj.type === 'trading') {
    type.value = '매매';
  } else if (dataObj.type == 'monthly') {
    type.value = '월세';
  } else {
    type.value = '전세';
  }
  detailObject.value = dataObj;
  console.log('detailObject : ', detailObject.value);
});

const detailModify = () => {
  router.push({
    name: 'managementModify',
    state: {
      dataObj: {
        ...detailObject.value,
      },
    },
  });
};
</script>

<style scoped>
.main-div {
  margin-top: 50px;
}

.detail-card {
  height: auto;
}

.price-card {
  height: auto;
}

.price-info,
.detail-info {
  margin-top: 20px;
}

.container-xl {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.detail-image {
  max-width: 300px; /* 이미지 크기를 줄임 */
  height: auto;
  display: block;
  margin: 0 auto;
}

.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 10px;
}

.detail-item {
  margin-bottom: 10px;
}

.detail-item label {
  font-weight: bold;
}

.card {
  border: 1px solid #ddd;
}

.card-body {
  padding: 10px;
}

.text-center {
  text-align: center;
}

.btn-primary {
  background-color: #8cc0de;
  border-color: #8cc0de;
  color: white;
}

.btn-primary:hover {
  background-color: #5bbaf1;
  border-color: #5bbaf1;
}

.btn-lg {
  padding: 10px 20px;
  font-size: 1.2rem;
}
</style>
