<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { findAllPrevChatMessage, deleteRoom } from '@/api/chatting.js';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';
import { httpStatusCode } from '@/util/http-status.js';

const route = useRoute();
const router = useRouter();
const roomObject = ref(JSON.parse(route.query.roomObject));
const store = useUserStore();
const { userInfo } = storeToRefs(store);
const prevMessage = ref([]);
const newMessage = ref('');
let stompClient = null;
const key = ref(0);
onMounted(() => {
  console.log('Room Object: ', roomObject.value);
  connect();
});

onBeforeUnmount(() => {
  disconnect();
});

function connect() {
  console.log('last Room Object = ', roomObject.value);
  const accessToken = sessionStorage.getItem('accessToken');
  const socket = new SockJS(`http://localhost:8080/ws/chat?accessToken=${accessToken}`);
  console.log('send User = ', roomObject.value.sender);
  const headers = {
    userId: roomObject.value.sender,
  };

  stompClient = Stomp.over(socket);
  stompClient.connect(headers, async function (frame) {
    console.log('Connected: ' + frame);
    sendEnterMessage();
    findAllPrevChatMessage(
      roomObject.value.roomId,
      (response) => {
        console.log('Response = ', response);
        prevMessage.value = response.data.list;
        if (response.status === httpStatusCode.ACCEPTED) {
          alert('상대방이 채팅을 종료하셨습니다.\n 이전 메시지를 읽는 것은 가능합니다.');
          stompClient.disconnect();
        }
      },
      (error) => {
        console.log('error =', error);
      }
    );
    stompClient.subscribe(
      '/user/' + roomObject.value.receiver + '/sub/' + roomObject.value.roomId,
      function (greeting) {
        console.log('greeting is =' + greeting);
        console.log('greetings body and content = ' + JSON.parse(greeting.body).content);
        console.log('greetings body = ', JSON.parse(greeting.body));
        showGreeting(JSON.parse(greeting.body));
      }
    );
    stompClient.subscribe('/sub/' + roomObject.value.roomId, function (greeting) {
      console.log('received ok');
      showGreeting(JSON.parse(greeting.body));
    });
  });
}

function sendEnterMessage() {
  let enterMessage = {
    type: 'ENTER',
    roomId: roomObject.value.roomId,
    sender: roomObject.value.receiver,
    receiver: roomObject.value.sender,
    message: '입장하였습니다.',
  };
  stompClient.send(`/pub/${roomObject.value.roomId}`, {}, JSON.stringify(enterMessage));
}

function showGreeting(message) {
  prevMessage.value.push(message);
  console.log('to message = ', prevMessage.value);
}

const sendMessage = () => {
  let talkMessage = {
    type: 'TALK',
    roomId: roomObject.value.roomId,
    sender: roomObject.value.receiver,
    receiver: roomObject.value.sender,
    message: newMessage.value,
  };
  stompClient.send(`/pub/${roomObject.value.roomId}`, {}, JSON.stringify(talkMessage));
  newMessage.value = '';
};

const disconnect = () => {
  if (stompClient !== null) {
    stompClient.disconnect();
    console.log('Disconnected');
  }
};
watch(
  () => route.query.roomObject,
  (newVal) => {
    roomObject.value = JSON.parse(newVal);
    console.log('Updated Room Object: ', roomObject.value);
    reconnect();
  }
);
const reconnect = () => {
  disconnect();
  prevMessage.value = [];
  connect();
};

const leaveChat = () => {
  disconnect();
  router.push({ name: 'chat' }); // 홈 페이지 또는 다른 페이지로 이동
};

const deleteChat = () => {
  const { userInfo } = storeToRefs(store);

  let sendObject = {
    roomId: roomObject.value.roomId,
    name: roomObject.value.name,
    sender: roomObject.value.sender,
    receiver: roomObject.value.receiver,
  };

  const userId = userInfo.value.userId;

  // 유저의 역할에 따라 sender와 receiver 값 조정
  if (userInfo.value.userRole === 'user') {
    sendObject = {
      ...sendObject,
      sender: roomObject.value.receiver,
      receiver: roomObject.value.sender,
    };
  }

  deleteRoom(
    userId,
    sendObject,
    (response) => {
      alert('채팅방을 나오셨습니다.');
      key.value += 1; // key 값을 변경하여 강제 재렌더링 트리거
      router.push({ name: 'chat', query: { key: key.value } });
    },
    (error) => {
      alert('에러 발생');
    }
  );
};
</script>

<template>
  <div class="chat-container">
    <div class="messages-container">
      <h2 class="messages-title">채팅방</h2>
      <ul class="message-list">
        <li
          v-for="(message, index) in prevMessage"
          :key="index"
          :class="{ sender: message.sender === roomObject.receiver, receiver: message.sender !== roomObject.receiver }"
        >
          <div
            :class="{
              'id-right': message.sender === roomObject.receiver,
              'id-left': message.sender !== roomObject.receiver,
            }"
          >
            {{ message.sender }}
          </div>
          <div class="message">
            <span>{{ message.message }}</span>
          </div>
          <div
            :class="{
              'time-right': message.sender === roomObject.receiver,
              'time-left': message.sender !== roomObject.receiver,
            }"
          >
            {{ message.messageDate }}
          </div>
        </li>
      </ul>
    </div>

    <div class="input-container">
      <input v-model="newMessage" placeholder="전송 할 메시지" />
      <button @click="sendMessage" class="send-button">전송</button>
    </div>

    <div class="button-container">
      <button class="btn btn-secondary leave-button" @click="leaveChat">돌아가기</button>
      <button class="btn btn-danger delete-button" @click="deleteChat">채팅방 삭제</button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
}

.messages-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #5bbaf1;
  margin-bottom: 10px;
}

.message-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.message-list li {
  display: flex;
  flex-direction: column;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-list .sender {
  align-items: flex-end;
}

.message-list .receiver {
  align-items: flex-start;
}

.message {
  max-width: 60%;
  padding: 10px;
  border-radius: 10px;
  margin-bottom: 5px;
}

.message-list .sender .message {
  background-color: #c0e0ff;
  color: #000;
}

.message-list .receiver .message {
  background-color: #fff;
  color: #000;
  border: 1px solid #ccc;
}

.id-right {
  align-self: flex-end;
  font-weight: bold;
}

.id-left {
  align-self: flex-start;
  font-weight: bold;
}

.time-right {
  align-self: flex-end;
  font-size: 0.8rem;
  color: #888;
}

.time-left {
  align-self: flex-start;
  font-size: 0.8rem;
  color: #888;
}

.input-container {
  display: flex;
  padding: 10px;
  background-color: #f1f1f1;
  border-top: 1px solid #ccc;
}

.input-container input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
}

.input-container .send-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-color: #8cc0de;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.input-container .send-button:hover {
  background-color: #5bbaf1;
}

.button-container {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.leave-button,
.delete-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.leave-button {
  background-color: #6c757d;
}

.leave-button:hover {
  background-color: #5a6268;
}

.delete-button {
  background-color: #dc3545;
}

.delete-button:hover {
  background-color: #c82333;
}
</style>
