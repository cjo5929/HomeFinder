<script setup>
import { onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';

const store = useUserStore();
const { userInfo } = storeToRefs(store);
const { storeChangeImage, storeDeleteImage, storeGetImage, storeUpdateUser, storeDeleteUser } = store;

const user = ref({});
const imageSrc = ref('');

onMounted(async () => {
  user.value = {
    ...userInfo.value,
  };
  console.log('parent user=', user.value);
  await storeGetImage(user.value.userId);
  imageSrc.value = userInfo.value.imageSrc;
});

const changeImage = async (image) => {
  const formData = new FormData();
  formData.append('image', image);
  formData.append('userId', userInfo.value.userId);
  await storeChangeImage(formData);
  await storeGetImage(user.value.userId);
  imageSrc.value = userInfo.value.imageSrc;
};

const deleteImage = async () => {
  await storeDeleteImage(userInfo.value.userId);
  await storeGetImage(user.value.userId);
  imageSrc.value = userInfo.value.imageSrc;
};

const updateUser = async (userObject) => {
  console.log('before update object = ', userObject);
  await storeUpdateUser(userObject);
};

const deleteUser = async (userPassword) => {
  const sendObject = {
    userId: userInfo.value.userId,
    userPassword: userPassword,
  };
  await storeDeleteUser(sendObject);
};
</script>

<template>
  <div class="container-xl mt-4">
    <div class="row">
      <div class="row text-center">
        <div class="profile-header-img mb-3">
          <img
            :src="`data:image/png;base64, ${imageSrc}`"
            alt="Profile Image"
            class="rounded-circle img-fluid"
            style="width: 150px; height: 150px"
          />
        </div>
        <div>{{ user.userId }}님 환영합니다.</div>
      </div>
      <div class="row d-flex lex-column">
        <div class="d-flex justify-content-center w-100">
          <ul class="nav nav-tabs mt-3">
            <li class="nav-item">
              <RouterLink class="nav-link" :to="{ name: 'info' }">회원관리</RouterLink>
            </li>
            <li class="nav-item" v-if="user.userRole === 'estate'">
              <RouterLink class="nav-link" :to="{ name: 'management' }">매물관리</RouterLink>
            </li>
            <li class="nav-item" v-if="user.userRole === 'user'">
              <RouterLink class="nav-link" :to="{ name: 'favorite' }">찜목록</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" :to="{ name: 'chat' }">채팅방</RouterLink>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="row mt-4">
      <div class="col-md-2 sidebars">
        <router-view name="sidebar" class="mt-5"></router-view>
      </div>
      <div class="col-lg-8">
        <RouterView
          name="default"
          :imageSrc="imageSrc"
          :user="user"
          @changeImage="changeImage"
          @deleteImage="deleteImage"
          @updateUser="updateUser"
          @deleteUser="deleteUser"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.sidebars {
  margin-top: 50px;
}
.container {
  display: flex;
  height: 100vh;
}

.profile-header-img {
  position: relative;
}

.profile-header-img img {
  border: 3px solid #ffffff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.nav-tabs .nav-link {
  font-weight: 600;
}

.nav-tabs .nav-link.active {
  color: #495057;
  background-color: #ffffff;
  border-color: #dee2e6 #dee2e6 #ffffff;
}
</style>
