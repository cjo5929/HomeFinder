<template>
  <div>
    <h3 class="text-center mb-4">찜 목록</h3>
    <button class="btn btn-primary mb-3" @click="compareStart" :disabled="selectedItems.length !== 2">찜 비교</button>
    <div class="row">
      <div class="col-md-6 col-lg-4 mb-4" v-for="(item, index) in favoriteList" :key="index">
        <div class="card h-100" :class="{ 'border-primary': selectedItems.includes(item) }" @click="toggleSelect(item)">
          <img :src="`data:image/png;base64, ${item.base64}`" class="card-img-top" alt="NO IMAGE">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">{{ item.name }}</h5>
            <p class="card-text">구분 : {{ item.type === "trading" ? "매매" : item.type === "monthly" ? "월세" : "전세" }}</p>
            <p class="card-text">가격 : {{ item.price }}원</p>
            <p class="card-text">층 : {{ item.floor }}층</p>
            <p class="card-text">면적 : {{ item.area }}m²</p>
            <p class="card-text">매물 등록일 : <br>{{ item.regist_date }}</p>
            <div class="mt-auto row">
              <button @click.stop="detail(item)" class="btn btn-primary col-5">자세히 보기</button> 
              <div class="col-2"></div>
              <button class="btn btn-primary col-5" @click="deleteFavorite(item.favorite_no)">찜 삭제</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal" tabindex="-1">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">매물 비교</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>구분</th>
                  <th>매물 1</th>
                  <th>매물 2</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>이미지</td>
                  <td><img :src="`data:image/png;base64, ${selectedItems[0].base64}`" class="compare-img" alt="NO IMAGE"></td>
                  <td><img :src="`data:image/png;base64, ${selectedItems[1].base64}`" class="compare-img" alt="NO IMAGE"></td>
                </tr>
                <tr>
                  <td>가격비교</td>
                  <td><deal-chart :dealList="selectedFirstDealList" title="매물 1 연도별 거래 금액"></deal-chart></td>
                  <td><deal-chart :dealList="selectedSecondDealList" title="매물 2 연도별 거래 금액"></deal-chart></td>
                </tr>
                <tr>
                  <td>이름</td>
                  <td>{{ selectedItems[0].name }}</td>
                  <td>{{ selectedItems[1].name }}</td>
                </tr>
                <tr>
                  <td>구분</td>
                  <td>{{ selectedItems[0].type === "trading" ? "매매" : selectedItems[0].type === "monthly" ? "월세" : "전세" }}</td>
                  <td>{{ selectedItems[1].type === "trading" ? "매매" : selectedItems[1].type === "monthly" ? "월세" : "전세" }}</td>
                </tr>
                <tr>
                  <td>가격</td>
                  <td>{{ selectedItems[0].price }}원</td>
                  <td>{{ selectedItems[1].price }}원</td>
                </tr>
                <tr>
                  <td>층</td>
                  <td>{{ selectedItems[0].floor }}층</td>
                  <td>{{ selectedItems[1].floor }}층</td>
                </tr>
                <tr>
                  <td>면적</td>
                  <td>{{ selectedItems[0].area }}m²</td>
                  <td>{{ selectedItems[1].area }}m²</td>
                </tr>
                <tr>
                  <td>매물 등록일</td>
                  <td>{{ selectedItems[0].regist_date }}</td>
                  <td>{{ selectedItems[1].regist_date }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Close</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/userStore.js";
import DealChart from '@/components/user/DealChart.vue';
import { getHouseDealListByAptCode2 } from "@/api/map.js";
import { APIdeleteFavorite } from "@/api/userFavorite.js";

const router = useRouter(); 
const store = useUserStore();
const { userInfo } = storeToRefs(store);
const props = defineProps({
  favoriteList: {
    type: Array,
    required: true,
  },
});
const emit = defineEmits(["deleteFavorite"]);
const deleteFavorite = (favorite_no) => {
  console.log("favorite_no = ",favorite_no);
  const userId = userInfo.value.userId;
  APIdeleteFavorite(userId, favorite_no, (response) => {
    alert("delete 성공");
    emit("deleteFavorite");
  }, (error) => {
    alert("삭제 실패");
  })
}

const selectedItems = ref([]);
const showModal = ref(false);

const toggleSelect = (item) => {
  if (selectedItems.value.includes(item)) {
    selectedItems.value = selectedItems.value.filter(i => i !== item);
  } else if (selectedItems.value.length < 2) {
    selectedItems.value.push(item);
  }
};
const selectedFirstDealList = ref([]);
const selectedSecondDealList = ref([]);
const compareStart = async () => {
  console.log("first =", selectedItems.value[0]);
  console.log("second =", selectedItems.value[1]);

  try {
    const firstResponse = await getHouseDealListByAptCode2(selectedItems.value[0].apt_id);
    
    const secondResponse = await getHouseDealListByAptCode2(selectedItems.value[1].apt_id);
    
    selectedFirstDealList.value = firstResponse.data;
    selectedSecondDealList.value = secondResponse.data;

    showModal.value = true;
  } catch (error) {
    console.error("Error fetching deal lists", error);
  }
};

const closeModal = () => {
  showModal.value = false;
};

const detail = (item) => {
  console.log('item = ', item);
  console.log("item.value = ",item.value);
  router.push({
    name: 'managementDetailFavorite',
    state: {
      dataObj: {
        base64 : item.base64,
        name : item.name,
        price : item.price,
        room : item.room,
        floor : item.floor,
        area : item.area,
        bath : item.bath,
        heater : item.heater,
        entrance : item.entrance,
        move_in : item.move_in,
        regist_date : item.regist_date,
        user_id : item.user_id,
      },
    }
  });
};
</script>

<style scoped>
.card {
  border: 1px solid #ddd;
  cursor: pointer;
}

.card.border-primary {
  border-color: #007bff;
}

.card-img-top {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.card-body {
  text-align: left;
  display: flex;
  flex-direction: column;
}

.card-body .mt-auto {
  margin-top: auto;
}

.text-center {
  color: #333;
}

.modal {
  display: flex;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  justify-content: center;
  align-items: center;
}

.modal-dialog {
  max-width: 1000px;
}

.modal-content {
  padding: 20px;
}

.compare-img {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: contain;
}

.table-bordered {
  border: 1px solid #ddd;
}

.table th, .table td {
  vertical-align: middle;
  text-align: center;
}
</style>