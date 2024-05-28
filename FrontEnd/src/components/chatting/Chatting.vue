<template>
  <div class="chatting-container">
    <div class="chatting-header">
      <h4>상담하기</h4>
      <button class="close-button" @click="closeChatting">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
          <path
            d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM175 175c9.4-9.4 24.6-9.4 33.9 0l47 47 47-47c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9l-47 47 47 47c9.4 9.4 9.4 24.6 0 33.9s-24.6 9.4-33.9 0l-47-47-47 47c-9.4 9.4-24.6 9.4-33.9 0s-9.4-24.6 0-33.9l47-47-47-47c-9.4-9.4-9.4-24.6 0-33.9z"
          />
        </svg>
      </button>
    </div>
    <div class="chatting-body">
      <ChattingList :messageList="messageList" @sendMessage="sendMessage" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, defineProps, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/userStore';
import ChattingList from '@/components/chatting/ChattingList.vue';
import { findRoomIdBySenderAndReceiver, findAllPrevChatMessage } from '@/api/chatting.js';

const emit = defineEmits(['close']);
const closeChatting = () => {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  emit('close');
};
let stompClient = null;
const store = useUserStore();
const { userInfo } = storeToRefs(store);

const props = defineProps({
  chattingObject: Object,
});

const chatObject = ref({});
const roomId = ref('');
const messageList = ref([]);
onMounted(async () => {
  console.log('props data = ', props.chattingObject);
  await findRoomIdBySenderAndReceiver(
    props.chattingObject,
    (response) => {
      console.log('response = ', response);
      roomId.value = response.data.roomId;
      chatObject.value.sender = props.chattingObject.sender;
      chatObject.value.receiver = props.chattingObject.receiver;
      chatObject.value.itemNo = props.chattingObject.itemNo;
      chatObject.value.roomId = roomId.value;
    },
    (error) => {
      console.log('error = ', error);
    }
  );
  connect();
});

function connect() {
  const accessToken = sessionStorage.getItem('accessToken');
  const socket = new SockJS(`http://localhost:8080/ws/chat?accessToken=${accessToken}`);
  console.log('send User = ', chatObject.value.sender);
  const headers = {
    userId: chatObject.value.sender,
  };

  stompClient = Stomp.over(socket);
  stompClient.connect(headers, async function (frame) {
    console.log('Connected: ' + frame);
    sendEnterMessage();
    findAllPrevChatMessage(
      chatObject.value.roomId,
      (response) => {
        console.log('Response = ', response);
        messageList.value = response.data.list;
      },
      (error) => {
        console.log('error =', error);
      }
    );
    stompClient.subscribe('/user/' + chatObject.value.sender + '/sub/' + chatObject.value.roomId, function (greeting) {
      console.log('greeting is =' + greeting);
      console.log('greetings body and content = ' + JSON.parse(greeting.body).content);
      console.log('greetings body = ', JSON.parse(greeting.body));
      showGreeting(JSON.parse(greeting.body));
    });
    stompClient.subscribe('/sub/' + chatObject.value.roomId, function (greeting) {
      console.log('received ok');
      showGreeting(JSON.parse(greeting.body));
    });
  });
}

function sendEnterMessage() {
  let enterMessage = {
    type: 'ENTER',
    roomId: chatObject.value.roomId,
    sender: chatObject.value.sender,
    receiver: chatObject.value.receiver,
    message: '입장하였습니다.',
  };
  stompClient.send(`/pub/${chatObject.value.roomId}`, {}, JSON.stringify(enterMessage));
}

function showGreeting(message) {
  messageList.value.push(message);
  console.log('to message = ', messageList.value);
}

const sendMessage = (message) => {
  console.log('message = ', message);
  let talkMessage = {
    type: 'TALK',
    roomId: chatObject.value.roomId,
    sender: chatObject.value.sender,
    receiver: chatObject.value.receiver,
    message: message,
  };
  stompClient.send(`/pub/${chatObject.value.roomId}`, {}, JSON.stringify(talkMessage));
};
</script>

<style scoped>
.chatting-container {
  border-left: 1px solid #ddd;
  padding: 20px;
  height: 100%;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.chatting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.chatting-header h4 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.close-button:hover {
  background-color: #f0f0f0;
}

.close-button svg {
  width: 24px;
  height: 24px;
  fill: #555;
  transition: fill 0.3s;
}

.close-button:hover svg {
  fill: #000;
}

.chatting-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #fff;
  border-radius: 10px;
}
</style>
