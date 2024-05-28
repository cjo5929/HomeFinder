<template>
  <div class="map_wrap">
    <KakaoMap
      :lat="lat"
      :lng="lng"
      @onLoadKakaoMap="onLoadKakaoMap"
      :draggable="true"
      style="width: 100%; height: 100vh"
      :level="8"
    >
    </KakaoMap>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { KakaoMap } from "vue3-kakao-maps";

// 지도에 표시할 영역 데이터 배열입니다
import area from "@/assets/data/seoul_sig.json";

const props = defineProps({
  selectedData: {
    type: Object,
    default: null,
  },
});

const map = ref();
const lat = ref(37.57002838826);
const lng = ref(126.97962084516);
let polygons = [];

const onLoadKakaoMap = async (mapRef) => {
  map.value = mapRef;

  // 병렬처리
  await Promise.all(area.features.map((feature) => displayArea(feature, props.selectedData)));
};

watch(
  () => props.selectedData,
  (newData) => {
    if (newData) {
      // 기존 폴리곤 제거
      polygons.forEach((polygon) => polygon.setMap(null));
      polygons = [];

      // 새로운 데이터로 폴리곤 생성
      area.features.forEach((feature) => {
        displayArea(feature, newData);
      });
    }
  }
);

// 순위 계산 => 5개의 등급으로 나누기
const getRank = (score, scores) => {
  const minScore = Math.min(...scores);
  const maxScore = Math.max(...scores);
  const rangeSize = (maxScore - minScore) / 5;

  const scaledScore = score * 100; // 점수를 100배로 확대

  if (scaledScore >= minScore * 100 + rangeSize * 400) {
    return 1;
  } else if (scaledScore >= minScore * 100 + rangeSize * 300) {
    return 2;
  } else if (scaledScore >= minScore * 100 + rangeSize * 200) {
    return 3;
  } else if (scaledScore >= minScore * 100 + rangeSize * 100) {
    return 4;
  } else {
    return 5;
  }
};
// 폴리곤 색상
const colors = [
  "rgb(0, 138, 224)",
  "rgb(51, 162, 231)",
  "rgb(103, 187, 239)",
  "rgb(155, 211, 246)",
  "rgb(207, 236, 254)",
];

const getColorByRank = (rank, totalRanks) => {
  const index = Math.floor((rank - 1) / (totalRanks / colors.length));
  return colors[index];
};

const displayArea = (feature, data) => {
  const coordinates = feature.geometry.coordinates[0];
  const path = coordinates.map(([lng, lat]) => new window.kakao.maps.LatLng(lat, lng));

  if (data) {
    const item = data.find((item) => item.dongCode.slice(0, 5) === feature.properties.SIG_CD);

    if (item) {
      console.log(item);
      let score = item.count !== undefined ? item.count : item.safetyScore;

      if (score === undefined) {
        console.log(`No score found for SIG_CD: ${feature.properties.SIG_CD}`);
        return;
      }

      const rank = getRank(
        score,
        data.map((item) => (item.count !== undefined ? item.count : item.safetyScore))
      );
      console.log(rank);
      const color = getColorByRank(rank, 5);

      const polygon = new window.kakao.maps.Polygon({
        path: path,
        strokeWeight: 3,
        strokeColor: "rgb(102, 102, 102)",
        strokeOpacity: 0.8,
        fillColor: color,
        fillOpacity: 0.6,
      });

      if (map.value !== undefined) {
        polygon.setMap(map.value);
        polygons.push(polygon);

        let isMouseOver = false;

        // 마우스 오버 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "mouseover", function () {
          if (!isMouseOver) {
            polygon.setOptions({
              fillColor: "#2EFE64",
              fillOpacity: 0.6,
            });
            isMouseOver = true;
          }
        });

        // 마우스 아웃 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "mouseout", function () {
          if (isMouseOver) {
            polygon.setOptions({
              fillColor: color,
              fillOpacity: 0.6,
            });
            isMouseOver = false;
          }
        });

        // 클릭 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "click", function () {
          // 폴리곤의 중심점 계산
          const points = polygon.getPath().map((coord) => ({
            x: coord.getLng(),
            y: coord.getLat(),
          }));
          const [centerLng, centerLat] = findCentroid(points);

          // 폴리곤의 중심점으로 지도 이동 및 확대
          lat.value = centerLat;
          lng.value = centerLng;
          map.value.setLevel(7); // 확대 레벨 설정 (숫자가 작을수록 더 확대됨)
        });
      }
    } else {
      console.log(`No data found for SIG_CD: ${feature.properties.SIG_CD}`);

      // 데이터를 찾지 못한 경우 기본 색상으로 폴리곤 생성
      const color = "rgb(207, 236, 254)";

      const polygon = new window.kakao.maps.Polygon({
        path: path,
        strokeWeight: 3,
        strokeColor: "rgb(102, 102, 102)",
        strokeOpacity: 0.8,
        fillColor: color,
        fillOpacity: 0.6,
      });

      if (map.value !== undefined) {
        polygon.setMap(map.value);
        polygons.push(polygon);

        let isMouseOver = false;

        // 마우스 오버 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "mouseover", function () {
          if (!isMouseOver) {
            polygon.setOptions({
              fillColor: "#2EFE64",
              fillOpacity: 0.6,
            });
            isMouseOver = true;
          }
        });

        // 마우스 아웃 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "mouseout", function () {
          if (isMouseOver) {
            polygon.setOptions({
              fillColor: color,
              fillOpacity: 0.6,
            });
            isMouseOver = false;
          }
        });

        // 클릭 이벤트 추가
        window.kakao.maps.event.addListener(polygon, "click", function () {
          // 폴리곤의 중심점 계산
          const points = polygon.getPath().map((coord) => ({
            x: coord.getLng(),
            y: coord.getLat(),
          }));
          const [centerLng, centerLat] = findCentroid(points);

          // 폴리곤의 중심점으로 지도 이동 및 확대
          lat.value = centerLat;
          lng.value = centerLng;
          map.value.setLevel(7); // 확대 레벨 설정 (숫자가 작을수록 더 확대됨)
        });
      }
    }
  }
};

// 폴리곤 중앙값 구하는 알고리즘
function findCentroid(points) {
  let i, j, len, p1, p2, f, area, x, y;
  area = x = y = 0;

  for (i = 0, len = points.length, j = len - 1; i < len; j = i++) {
    p1 = points[i];
    p2 = points[j];

    f = p1.y * p2.x - p2.y * p1.x;
    x += (p1.x + p2.x) * f;
    y += (p1.y + p2.y) * f;
    area += f * 3;
  }

  return [x / area, y / area];
}
</script>

<style scoped></style>
