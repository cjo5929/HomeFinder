<script setup>
import { onMounted, ref, watch} from 'vue';
import { useUserStore } from "@/stores/userStore.js";
import {useRouter} from 'vue-router';
import { storeToRefs } from "pinia";
import { findAllRoomByUserId } from "@/api/chatting.js";

const chatRoomList = ref([]);
const router = useRouter();
const props = defineProps(['key']);

const fetchData = async () => {
  const store = useUserStore();
  const { userInfo } = storeToRefs(store);
  const userId = userInfo.value.userId;
  console.log("userId = " + userId);
  await findAllRoomByUserId(userId, (response) => {
    console.log("response = ", response);
    chatRoomList.value = response.data.list;
  }, (error) => {
    console.log("error = ", error);
  });
  console.log("room List = ", chatRoomList.value);
};

onMounted(() => {
  fetchData();
});

watch(() => props.key, () => {
  fetchData();
});
const openChatting = (roomObject) => {
  const store = useUserStore();
  const {userInfo} = storeToRefs(store);
  let adjustedRoomObject = {
    roomId: roomObject.roomId,
    sender: roomObject.sender,
    receiver: roomObject.receiver,
    name: roomObject.name,
    itemNo: roomObject.itemNo
  };

  if (userInfo.value.userRole === 'user') {
    
    adjustedRoomObject = {
      ...adjustedRoomObject,
      sender: roomObject.receiver,
      receiver: roomObject.sender
    };
  }
  
  console.log("before send = ", adjustedRoomObject);
  router.push({
    name: "chatDetail", 
    query: {
      roomObject: JSON.stringify(adjustedRoomObject)
    }
  });
}
</script>

<template>
  <div>
    <h3>Chat Rooms</h3>
    <ul>
      <li v-for="(room, index) in chatRoomList" :key="index" @click="openChatting(room)">
        {{ room.sender }}가 보낸 메시지
      </li>
    </ul>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
}

li {
  background-color: #f9f9f9;
  margin: 0.5rem 0;
  padding: 1rem;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
</style>