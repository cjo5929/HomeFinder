<template>
  <div ref="roadviewContainer" style="width: 100%; height: 400px"></div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { isKakaoMapApiLoaded } from "vue3-kakao-maps/@utils";
const roadviewContainer = ref(null);

const props = defineProps({
  apt: {
    type: Object,
    required: true,
  },
});

onMounted(() => {
  if (props.apt) {
    onMarkerClick(props.apt);
  }
});

watch(
  () => props.apt,
  (newApt) => {
    if (newApt) {
      onMarkerClick(newApt);
    }
  }
);

const onMarkerClick = (apt) => {
  if (isKakaoMapApiLoaded.value) {
    showRoadview({ lat: apt.lat, lng: apt.lng });
  }
};

const showRoadview = (position) => {
  if (roadviewContainer.value) {
    const roadviewClient = new window.kakao.maps.RoadviewClient();

    roadviewClient.getNearestPanoId(
      new window.kakao.maps.LatLng(position.lat, position.lng),
      50,
      (panoId) => {
        if (panoId === null) {
          console.log("로드뷰 정보를 찾을 수 없습니다.");
          return;
        }

        const roadview = new window.kakao.maps.Roadview(roadviewContainer.value);
        const roadviewPosition = new window.kakao.maps.LatLng(position.lat, position.lng);

        roadview.setPanoId(panoId, roadviewPosition);
      }
    );
  }
};
</script>

<style scoped></style>
