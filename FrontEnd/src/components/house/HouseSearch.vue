<template>
  <div class="search-box">
    <div class="select-container">
      <select class="form-select" v-model="selectedSido" @change="getGugunListBySido">
        <option value="">시/도 선택</option>
        <option v-for="sido in sidoList" :key="sido" :value="sido">{{ sido }}</option>
      </select>
      <select
        class="form-select"
        v-model="selectedGugun"
        @change="getDongListByGugun"
        :disabled="!selectedSido"
      >
        <option value="">구/군 선택</option>
        <option v-for="gugun in gugunList" :key="gugun" :value="gugun">{{ gugun }}</option>
      </select>
      <select class="form-select" v-model="selectedDong" :disabled="!selectedGugun">
        <option value="">동 선택</option>
        <option v-for="dong in dongList" :key="dong" :value="dong">{{ dong }}</option>
      </select>
      <button class="btn btn-primary" @click="searchByAddress">
        <font-awesome-icon icon="fa-solid fa-search" />
      </button>
    </div>
    <div class="apt-search">
      <input
        type="text"
        class="form-control"
        v-model="searchName"
        placeholder="아파트명으로 검색"
      />
      <button class="btn btn-primary" @click="searchByName">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass-location" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import {
  getSidoList,
  getGugunList,
  getDongList,
  getApartListByName,
  getApartListByAddress,
} from "@/api/map.js";

const sidoList = ref([]);
const gugunList = ref([]);
const dongList = ref([]);
const selectedSido = ref("");
const selectedGugun = ref("");
const selectedDong = ref("");
const searchName = ref("");
const aptList = ref([]);

const emit = defineEmits(["update-apt-list"]);

onMounted(() => {
  getSidoList(
    ({ data }) => {
      sidoList.value = data;
    },
    (error) => {
      console.error(error);
    }
  );
});

const getGugunListBySido = () => {
  if (selectedSido.value) {
    getGugunList(
      selectedSido.value,
      ({ data }) => {
        gugunList.value = data;
      },
      (error) => {
        console.error(error);
      }
    );
  } else {
    gugunList.value = [];
  }
  selectedGugun.value = "";
  selectedDong.value = "";
};

const getDongListByGugun = () => {
  if (selectedGugun.value) {
    getDongList(
      selectedSido.value,
      selectedGugun.value,
      ({ data }) => {
        dongList.value = data;
      },
      (error) => {
        console.error(error);
      }
    );
  } else {
    dongList.value = [];
  }
  selectedDong.value = "";
};

const searchByAddress = () => {
  const addressName = {
    sidoName: selectedSido.value,
    gugunName: selectedGugun.value,
    dongName: selectedDong.value,
  };

  getApartListByAddress(
    addressName,
    ({ data }) => {
      aptList.value = data;
    },
    (error) => {
      console.error(error);
    }
  );
};

watch(aptList, (newList) => {
  emit("update-apt-list", newList);
  searchName.value = "";
});

const searchByName = () => {
  getApartListByName(
    searchName.value,
    ({ data }) => {
      aptList.value = data;
    },
    (error) => {
      console.error(error);
    }
  );
};
</script>

<style scoped></style>
