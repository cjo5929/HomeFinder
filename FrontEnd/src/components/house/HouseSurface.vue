<!-- HouseSurface.vue -->
<template>
  <div class="selection-panel">
    <div class="selection-grid">
      <div
        v-for="item in selectionItems"
        :key="item.label"
        class="selection-item"
        :class="{ active: selectedItem === item.label }"
        @click="selectItem(item.label)"
      >
        <font-awesome-icon :icon="item.icon" :style="{ color: item.color }" />
        <span>{{ item.label }}</span>
      </div>
    </div>
    <button class="query-button" @click="onQuery">조회</button>
    <div class="level-indicator">
      <div v-for="(level, index) in levels" :key="index" :class="'level level-' + (index + 1)">
        <span>{{ level }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faUsers,
  faShieldAlt,
  faCartArrowDown,
  faGraduationCap,
} from "@fortawesome/free-solid-svg-icons";

import { getSafetyScore, getFacilitiesList, getEducationList, getPopulationList } from "@/api/map";

const emit = defineEmits(["update-polygons"]);
const selectionItems = [
  { label: "인구밀집", icon: faUsers, color: "purple" },
  { label: "치안", icon: faShieldAlt, color: "orange" },
  { label: "생활편의", icon: faCartArrowDown, color: "red" },
  { label: "교육", icon: faGraduationCap, color: "skyblue" },
];

const selectedItem = ref(null);
const selectedData = ref(null);

const selectItem = (item) => {
  selectedItem.value = item;
};

const onQuery = () => {
  console.log("Selected item:", selectedItem.value);

  switch (selectedItem.value) {
    case "인구밀집":
      getPopulationList(
        (response) => {
          selectedData.value = response.data;
          emit("update-polygons", selectedData.value);
        },
        (error) => {
          console.error("Error fetching population data:", error);
        }
      );
      break;
    case "치안":
      getSafetyScore(
        (response) => {
          selectedData.value = response.data;
          emit("update-polygons", selectedData.value);
        },
        (error) => {
          console.error("Error fetching safety score data:", error);
        }
      );
      break;
    case "교육":
      getEducationList(
        (response) => {
          selectedData.value = response.data;
          emit("update-polygons", selectedData.value);
        },
        (error) => {
          console.error("Error fetching education data:", error);
        }
      );
      break;
    case "생활편의":
      getFacilitiesList(
        (response) => {
          selectedData.value = response.data;
          emit("update-polygons", selectedData.value);
        },
        (error) => {
          console.error("Error fetching facilities data:", error);
        }
      );
      break;
  }
};

const levels = ["5등급", "4등급", "3등급", "2등급", "1등급"];
</script>

<style scoped></style>
