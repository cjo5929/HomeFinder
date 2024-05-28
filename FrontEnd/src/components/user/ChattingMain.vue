<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { findAllPrevChatMessage } from '@/api/chatting.js';

const route = useRoute();
const router = useRouter();
const roomObject = ref(JSON.parse(route.query.roomObject));

const prevMessage = ref([]);
const newMessage = ref('');
let stompClient = null;

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
</script>

<template>
  <div class="chat-container">
    <h1>Chat Detail</h1>
    <p>Room ID: {{ roomObject.roomId }}</p>
    <p>Sender: {{ roomObject.sender }}</p>
    <p>Receiver: {{ roomObject.receiver }}</p>
    <p>Name: {{ roomObject.name }}</p>
    <p>Item No: {{ roomObject.itemNo }}</p>

    <div>
      <h2>Previous Messages</h2>
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
      <input v-model="newMessage" placeholder="Type your message here" />
      <button @click="sendMessage">Send</button>
    </div>

    <div>
      <button class="leave-button" @click="leaveChat">Leave Chat</button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 100vh;
  overflow: hidden;
}

.message-list {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
}

.message-list li {
  display: flex;
  flex-direction: column;
  padding: 5px 10px;
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
  margin-bottom: 5px;
}

.id-left {
  align-self: flex-start;
  margin-bottom: 5px;
}

.time-right {
  align-self: flex-end;
  font-size: 0.8rem;
  color: #888;
  margin-top: -5px;
}

.time-left {
  align-self: flex-start;
  font-size: 0.8rem;
  color: #888;
  margin-top: -5px;
}

.input-container {
  display: flex;
  padding: 10px;
  background-color: #f1f1f1;
  position: sticky;
  bottom: 0;
  width: 100%;
}

.input-container input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
}

.input-container button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
}

.input-container button:hover {
  background-color: #0056b3;
}

.leave-button {
  background-color: #f44336;
  margin-top: 20px;
}

.leave-button:hover {
  background-color: #e53935;
}
</style>
