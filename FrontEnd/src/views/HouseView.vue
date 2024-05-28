<template>
  <div class="container-fluid p-0 main-container">
    <div class="row no-gutters">
      <div class="col-3 info-container">
        <div class="tab-container">
          <div class="tab" :class="{ active: activeTab === 'home' }" @click="activeTab = 'home'">
            <font-awesome-icon :icon="['fas', 'home']" />
            <span>매물 보기</span>
          </div>
          <div class="tab" :class="{ active: activeTab === 'map' }" @click="activeTab = 'map'">
            <font-awesome-icon :icon="['fas', 'map']" />
            <span>지역 통계</span>
          </div>
        </div>
        <div v-if="activeTab === 'home'">
          <HouseDetail
            @update-apt-list="updateAptList"
            @select-apt="selectApt"
            :apt="selectedApt"
            @openChatting="openChatting"
          />
        </div>
        <HouseSurface v-if="activeTab === 'map'" @update-polygons="updatePolygons" />
      </div>
      <div v-if="showChatting" class="col-2 chat-container">
        <Chatting @close="closeChatting" :chattingObject="chattingObject" />
      </div>
      <div :class="['col', showChatting ? 'col-7' : 'col-9', 'p-0', 'map-container']">
        <VKaKaoMap
          v-if="activeTab === 'home'"
          :selected-apt="selectedApt"
          :apt-list="aptList"
          @select-apt="selectApt"
        />
        <VKakaoMapPolygon v-if="activeTab === 'map'" :selected-data="selectedData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import VKaKaoMap from '@/components/house/VKaKaoMap.vue';
import HouseDetail from '@/components/house/HouseDetail.vue';
import VKakaoMapPolygon from '@/components/house/VKakaoMapPolygon.vue';
import HouseSurface from '@/components/house/HouseSurface.vue';
import Chatting from '@/components/chatting/Chatting.vue';
import { addChattingRoom } from '@/api/chatting.js';
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/userStore.js';

const store = useUserStore();
const { userInfo } = storeToRefs(store);

const selectedApt = ref(null);
const aptList = ref([]);
const showChatting = ref(false);

const chattingObject = ref({});
const openChatting = async (sendObject) => {
  console.log('homeview user Id=', sendObject);
  let senderAndReceiver = {
    sender: userInfo.value.userId,
    receiver: sendObject.userId,
    itemNo: sendObject.itemNo,
  };
  chattingObject.value = {
    ...senderAndReceiver,
  };
  console.log('object = ', senderAndReceiver);
  await addChattingRoom(
    senderAndReceiver,
    (response) => {},
    (error) => {}
  );
  showChatting.value = true;
};

const closeChatting = () => {
  showChatting.value = false;
};

const selectApt = (apt) => {
  selectedApt.value = apt;
  console.log(selectedApt.value);
};

const updateAptList = (list) => {
  aptList.value = list;
};

const activeTab = ref('home');
const selectedData = ref(null);

const updatePolygons = (data) => {
  selectedData.value = data;
};
</script>

<style scoped>
.info-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow-y: auto;
}

.tab-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.tab {
  cursor: pointer;
}

.tab.active {
  font-weight: bold;
}

.main-container {
  height: 100vh;
  overflow: hidden;
}

.chat-container {
  max-height: 100vh;
  overflow-y: auto;
  padding: 0;
}

.map-container {
  height: 100vh;
  overflow: hidden;
}
</style>
