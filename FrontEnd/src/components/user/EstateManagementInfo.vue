<template>
  <div class="container-xl mt-5">
    <div class="row">
      <h1 class="text-center mb-4">매물 관리</h1>
    </div>
    <hr />
    <div class="row">
      <div v-if="estateList.length === 0" class="col-12">
        <div class="no-items-container text-center">
          <img src="@/assets/no-items-apartment.png" alt="No Items" class="no-items-image" />
          <p class="no-items-text">등록한 매물이 현재 없습니다</p>
        </div>
      </div>
      <div v-else>
        <div class="row">
          <div class="col-md-4 mb-4" v-for="(item, index) in estateList" :key="index">
            <div class="card h-100 custom-card-width">
              <img :src="`data:image/png;base64, ${item.base64}`" alt="Apartment Image" class="card-img-top" />
              <div class="card-body">
                <h5 class="card-title">{{ item.name }}</h5>
                <p class="card-text">가격: {{ item.price }}원</p>
                <p class="card-text">동: {{ item.dong_name }}</p>
                <p class="card-text">등록일: {{ item.regist_date }}</p>
                <!-- 필요한 다른 속성들을 추가로 출력 -->
              </div>
              <div class="card-footer d-flex justify-content-between">
                <button @click="detail(item)" class="btn btn-primary">상세보기</button>
                <button @click="deleteEstate(item.item_no)" class="btn btn-danger">삭제</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch, computed } from 'vue';
import { useEstateStore } from '@/stores/estateStore';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const store = useEstateStore();
const userStore = useUserStore();
const { storeFindAllItem, storeDeleteItem } = store;
const { userInfo } = storeToRefs(userStore);
const { estateItem } = storeToRefs(store);
const router = useRouter();
const estateList = ref([]);

const rows = computed(() => {
  const result = [];
  for (let i = 0; i < estateList.value.length; i += 3) {
    result.push(estateList.value.slice(i, i + 3));
  }
  return result;
});

onMounted(async () => {
  await storeFindAllItem(userInfo.value.userId);
  estateList.value = estateItem.value;
  console.log(estateList.value);
});

watch(estateItem, (newValue, oldValue) => {
  estateList.value = estateItem.value;
});

const deleteEstate = (item_no) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    console.log(item_no);
    const sendDto = {
      userId: userInfo.value.userId,
      item_no: item_no,
    };
    storeDeleteItem(sendDto, (response) => {
      router.replace({ path: '/redirect' }).then(() => {
        router.replace({ name: 'management' });
      });
    });
  }
};
const detail = (item) => {
  console.log('item = ', item);
  router.push({
    name: 'managementDetail',
    state: {
      dataObj: {
        ...item,
      },
    },
  });
};
</script>

<style scoped>
.container-xl {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.custom-card-width {
  width: 100%; /* 카드의 너비를 100%로 설정하여 그리드 시스템에 맞추기 */
}

.card-img-top {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.card-body {
  text-align: left;
}

.card-footer {
  display: flex;
  justify-content: space-between;
}

.no-items-container {
  padding: 50px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #dee2e6;
}

.no-items-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-bottom: 20px;
}

.no-items-text {
  font-size: 1.5rem;
  color: #6c757d;
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

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
  border-color: #bd2130;
}

.btn-secondary {
  background-color: #6c757d;
  border-color: #6c757d;
  color: white;
}
</style>
