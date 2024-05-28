<template>
  <div class="chat-container">
    <ul class="message-list">
      <li
        v-for="(message, index) in messageList"
        :key="index"
        :class="{ sender: message.sender === userInfo.userId, receiver: message.sender !== userInfo.userId }"
      >
        <div
          class="sender-info"
          :class="{ sender: message.sender === userInfo.userId, receiver: message.sender !== userInfo.userId }"
        >
          {{ message.sender }}
        </div>
        <div
          class="message"
          :class="{ sender: message.sender === userInfo.userId, receiver: message.sender !== userInfo.userId }"
        >
          <span>{{ message.message }}</span>
        </div>
        <div
          class="message-date"
          :class="{ sender: message.sender === userInfo.userId, receiver: message.sender !== userInfo.userId }"
        >
          {{ message.messageDate }}
        </div>
      </li>
    </ul>
    <div class="input-container">
      <input type="text" v-model="message" placeholder="전송 할 메세지" />
      <button class="btn btn-primary" @click="sendMessage">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
          <path
            d="M498.1 5.6c10.1 7 15.4 19.1 13.5 31.2l-64 416c-1.5 9.7-7.4 18.2-16 23s-18.9 5.4-28 1.6L284 427.7l-68.5 74.1c-8.9 9.7-22.9 12.9-35.2 8.1S160 493.2 160 480V396.4c0-4 1.5-7.8 4.2-10.7L331.8 202.8c5.8-6.3 5.6-16-.4-22s-15.7-6.4-22-.7L106 360.8 17.7 316.6C7.1 311.3 .3 300.7 0 288.9s5.9-22.8 16.1-28.7l448-256c10.7-6.1 23.9-5.5 34 1.4z"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/userStore.js';
const store = useUserStore();
const { userInfo } = storeToRefs(store);
const props = defineProps({
  messageList: {
    type: Array,
    required: true,
  },
});
const emit = defineEmits(['sendMessage']);
const sendMessage = () => {
  if (message.value.trim()) {
    emit('sendMessage', message.value);
    message.value = '';
  }
};
const message = ref('');

console.log('list = ', props.messageList);
</script>

<style scoped>
.chat-container {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.message-list {
  flex: 1;
  list-style: none;
  padding: 10px;
  margin: 0;
  overflow-y: auto;
}

.message-list li {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.sender-info {
  font-weight: bold;
  font-size: 0.9rem;
}

.message-list .sender-info.sender {
  align-self: flex-end;
}

.message-list .sender-info.receiver {
  align-self: flex-start;
}

.message {
  max-width: 60%;
  padding: 10px;
  border-radius: 10px;
  margin: 5px 0;
  word-wrap: break-word;
}

.message-list .sender .message {
  background-color: #c0e0ff;
  align-self: flex-end;
}

.message-list .receiver .message {
  background-color: #fff;
  border: 1px solid #ccc;
  align-self: flex-start;
}

.message-date {
  font-size: 0.8rem;
  color: #888;
}

.message-list .sender .message-date {
  align-self: flex-end;
}

.message-list .receiver .message-date {
  align-self: flex-start;
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

.input-container button {
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-container button svg {
  width: 16px;
  height: 16px;
  margin-left: 5px;
  fill: #fff;
}

.input-container button:hover {
  background-color: #0056b3;
}
</style>
