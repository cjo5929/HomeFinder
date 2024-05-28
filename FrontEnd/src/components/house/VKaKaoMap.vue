<template>
  <div class="map_wrap">
    <KakaoMap
      :lat="lat"
      :lng="lng"
      @onLoadKakaoMap="onLoadKakaoMap"
      :draggable="true"
      style="width: 100%; height: 100%"
    >
      <KakaoMapMarker
        v-for="(marker, index) in markerList"
        :key="marker.key === undefined ? index : marker.key"
        :lat="marker.lat"
        :lng="marker.lng"
        :infoWindow="marker.infoWindow"
        :clickable="true"
        :image="{
          imageSrc: './img/makerImg.png',
          imageWidth: 42,
          imageHeight: 42,
          imageOption: {},
        }"
        @mouseOverKakaoMapMarker="mouseOverKakaoMapMarker(marker)"
        @mouseOutKakaoMapMarker="mouseOutKakaoMapMarker(marker)"
        @onClickKakaoMapMarker="aptDealInfo(marker)"
      />
      <KakaoMapMarker
        v-for="(result, index) in searchResults"
        :key="result.key === undefined ? index : result.key"
        :lat="result.lat"
        :lng="result.lng"
        :image="{
          imageWidth: 42,
          imageHeight: 42,
          imageOption: {},
        }"
      />
    </KakaoMap>

    <ul id="category">
      <li :class="{ on: activeCategory === 'BK9' }" @click="changeCategory('BK9')">
        <font-awesome-icon :icon="['fas', 'building-columns']" />
        <span>은행</span>
      </li>
      <li :class="{ on: activeCategory === 'MT1' }" @click="changeCategory('MT1')">
        <font-awesome-icon :icon="['fas', 'store']" />
        <span>마트</span>
      </li>
      <li :class="{ on: activeCategory === 'PM9' }" @click="changeCategory('PM9')">
        <font-awesome-icon :icon="['fas', 'prescription-bottle-medical']" />
        <span>약국</span>
      </li>
      <li :class="{ on: activeCategory === 'CE7' }" @click="changeCategory('CE7')">
        <font-awesome-icon :icon="['fas', 'mug-saucer']" />
        <span>카페</span>
      </li>
      <li :class="{ on: activeCategory === 'CS2' }" @click="changeCategory('CS2')">
        <font-awesome-icon :icon="['fas', 'shop']" />
        <span>편의점</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps';
import { isKakaoMapApiLoaded } from 'vue3-kakao-maps/@utils';

const map = ref();
const markerList = ref([]);
const selectedCategory = ref('');
const searchResults = ref([]);
const selectedPlace = ref(null);
const activeCategory = ref('');
const lat = ref(37.57002838826);
const lng = ref(126.97962084516);

const props = defineProps({
  aptList: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['select-apt']);

const onLoadKakaoMap = async (mapRef) => {
  map.value = mapRef;
};

watch(
  () => props.aptList,
  (newList) => {
    markerList.value = newList.map((apt) => ({
      key: apt.aptCode,
      lat: apt.lat,
      lng: apt.lng,
      infoWindow: {
        content: apt.apartmentName,
        visible: false,
      },
      area: apt.exclusiveArea,
      price: apt.dealAmount,
    }));

    if (markerList.value.length > 0) {
      const bounds = new window.kakao.maps.LatLngBounds();

      markerList.value.forEach((marker) => {
        bounds.extend(new window.kakao.maps.LatLng(marker.lat, marker.lng));
      });

      map.value.setBounds(bounds);
    }
  }
);

// 클린한 마커 정보 부모(HouseView)에게 전달
const aptDealInfo = (apt) => {
  emit('select-apt', apt);
};

const mouseOverKakaoMapMarker = (markerItem) => {
  markerItem.infoWindow.visible = true;
};

const mouseOutKakaoMapMarker = (markerItem) => {
  markerItem.infoWindow.visible = false;
};

const changeCategory = (category) => {
  if (selectedCategory.value === category) {
    selectedCategory.value = '';
    activeCategory.value = '';
    removeMarkers();
  } else {
    selectedCategory.value = category;
    activeCategory.value = category;
    searchPlaces();
  }
};

const searchPlaces = () => {
  if (map.value && isKakaoMapApiLoaded.value && selectedCategory.value) {
    const ps = new window.kakao.maps.services.Places();

    const callback = (result, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        removeMarkers(); // 이전 마커 제거
        searchResults.value = result.map((place) => ({
          key: place.id,
          lat: place.y,
          lng: place.x,
          place,
          marker: null,
          infoWindow: {
            content: place.place_name,
            visible: false,
          },
        }));
        displayMarkers();
      }
    };

    const mapCenter = map.value.getCenter();
    ps.categorySearch(selectedCategory.value, callback, {
      location: new window.kakao.maps.LatLng(mapCenter.getLat(), mapCenter.getLng()),
    });
  } else {
    removeMarkers();
    searchResults.value = [];
  }
};

const displayMarkers = () => {
  searchResults.value.forEach((result) => {
    const markerPosition = new window.kakao.maps.LatLng(result.lat, result.lng);
    const marker = addMarker(markerPosition, selectedCategory.value);

    window.kakao.maps.event.addListener(marker, 'click', () => {
      selectedPlace.value = result.place;
    });

    result.marker = marker;
  });
};

const addMarker = (position, category) => {
  let imageSrc = null;
  const imageSize = new window.kakao.maps.Size(42, 42);

  switch (category) {
    case 'BK9':
      imageSrc = '/img/markerBank.svg';
      break;
    case 'MT1':
      imageSrc = '/img/markerMarket.svg';
      break;
    case 'PM9':
      imageSrc = '/img/markerPhar.svg';
      break;
    case 'CE7':
      imageSrc = '/img/markerCafe.svg';
      break;
    case 'CS2':
      imageSrc = '/img/markerStore.svg';
      break;
    default:
      imageSrc = '/img/makerImg.png';
  }

  const markerImage = new window.kakao.maps.MarkerImage(imageSrc, imageSize);

  const marker = new window.kakao.maps.Marker({
    position: position,
    image: markerImage,
    clickable: true,
  });

  marker.setMap(map.value);

  return marker;
};

const removeMarkers = () => {
  searchResults.value = searchResults.value.filter((result) => {
    if (result.marker && result.marker instanceof window.kakao.maps.Marker) {
      result.marker.setMap(null); // 마커 제거
      return false; // 이 요소는 제외
    }
    return true; // 이 요소는 포함
  });
};
</script>

<style scoped></style>
