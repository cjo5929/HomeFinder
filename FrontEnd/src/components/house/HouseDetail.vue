<template>
  <!--HouseDetail.vue-->
  <HouseSearch @update-apt-list="updateAptList"></HouseSearch>
  <div v-if="apartDetail" class="detail-container">
    <HouseDetailRoad :apt="apt"></HouseDetailRoad>
    <div class="detail-header mt-2">
      <h4>{{ apartDetail.apartmentName }}</h4>
    </div>
    <img src="/public/img/defalutApt.jpg" alt="아파트 이미지" class="apt-image" />
    <div class="detail-info">
      <p>주소: {{ apartDetail.roadName }}</p>
      <p>건축년도: {{ apartDetail.buildYear }}</p>
      <p>매물 수: {{ apartDetail.dealCount }}</p>
    </div>
    <HouseDetailGraph :dealList="dealList"></HouseDetailGraph>
    <div class="estate-list-wrapper">
      <HouseDetailEstate :estateApartList="estateApartList" @openChatting="openChatting"></HouseDetailEstate>
    </div>
    <div class="deal-list">
      <h5>매물 리스트</h5>
      <table class="table">
        <thead>
          <tr>
            <th>거래일</th>
            <th>거래금액</th>
            <th>전용면적</th>
            <th>층수</th>
          </tr>
        </thead>
        <tbody>
          <HouseDetailItem v-for="deal in dealList" :key="deal.dealId" :deal="deal" />
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import HouseDetailItem from '@/components/house/HouseDetailItem.vue';
import HouseDetailGraph from './HouseDetailGraph.vue';
import HouseDetailRoad from './HouseDetailRoad.vue';
import HouseSearch from './HouseSearch.vue';
import HouseDetailEstate from '@/components/house/HouseDetailEstate.vue';
import { getHouseDealListByAptCode, getAptInfoDetailByAptCode, getEstateApartListByAptCode } from '@/api/map.js';

const apartDetail = ref(null);
const dealList = ref([]);
const aptList = ref([]);

const emit = defineEmits(['update-apt-list', 'openChatting']);
const openChatting = (sendObject) => {
  console.log('detail User id =', sendObject);
  emit('openChatting', sendObject);
};

const updateAptList = (list) => {
  aptList.value = list;
};

watch(aptList, (newList) => {
  emit('update-apt-list', newList);
});

const props = defineProps({
  apt: {
    type: Object,
    required: true,
  },
});

watch(
  () => props.apt,
  (newApt) => {
    if (newApt) {
      fetchApartDetail(newApt);
      fetchDealList(newApt.key);
      fetchApartEstate(newApt.key);
    } else {
      apartDetail.value = null;
      dealList.value = [];
    }
  }
);

const fetchDealList = (aptCode) => {
  getHouseDealListByAptCode(
    aptCode,
    ({ data }) => {
      if (data) {
        dealList.value = data;
      } else {
        dealList.value = [];
      }
    },
    (error) => {
      console.error(error);
      dealList.value = [];
    }
  );
};

const fetchApartDetail = (apt) => {
  getAptInfoDetailByAptCode(
    apt.key,
    ({ data }) => {
      if (data) {
        apartDetail.value = data;
      } else {
        apartDetail.value = null;
      }
    },
    (error) => {
      console.error(error);
      apartDetail.value = null;
    }
  );
};

const estateApartList = ref([]);

const fetchApartEstate = (apt_id) => {
  estateApartList.value = [];
  getEstateApartListByAptCode(
    apt_id,
    (response) => {
      console.log(response);
      if (response.data.list.length === 0) {
        estateApartList.value = [];
        console.log('리스트가 비어있습니다.');
      } else {
        // 리스트에 데이터가 있는 경우 처리
        response.data.list.forEach((item) => {
          estateApartList.value.push(item);
        });
      }
      console.log('estate = ', estateApartList.value);
    },
    (error) => {
      // 에러 처리
    }
  );
};
</script>

<style scoped>
.detail-container {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin: 20px;
}
.detail-header {
  background-color: #8cc0de;
  color: white;
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 20px;
}
.apt-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 20px;
}
.detail-info {
  background-color: #ffffff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
.detail-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #333;
}
.estate-list-wrapper {
  overflow-x: auto;
  white-space: nowrap;
  width: 100%;
  margin: 20px 0;
  padding: 10px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.deal-list {
  background-color: #ffffff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
.deal-list h5 {
  margin-bottom: 10px;
}
.table {
  width: 100%;
  border-collapse: collapse;
}
.table th,
.table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}
.table th {
  background-color: #f2f2f2;
  font-weight: bold;
}
</style>
