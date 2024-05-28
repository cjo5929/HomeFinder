<script setup>
import { onMounted, ref, defineProps, defineEmits, watch } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { storeToRefs } from 'pinia';

const props = defineProps({
  user: Object,
  imageSrc: String,
});
const emit = defineEmits(['updateUser', 'changeImage', 'deleteImage']);
const store = useUserStore();
const { userInfo } = storeToRefs(store);
const { storeUpdateUser } = store;

const modifyUser = ref({
  userId: '',
  userPassword: '',
  userAddress: '',
  userEmail: '',
  userPhone: '',
  registDate: '',
  userRole: '',
});

const passwordMatchMessage = ref('');
const validation = ref(false);

const selectedImg = ref(null);
const showInfo = ref(false); // info 이미지의 정보 표시 여부

onMounted(() => {
  modifyUser.value = {
    userId: props.user.userId,
    userAddress: props.user.userAddress,
    userEmail: props.user.userEmail,
    userPhone: props.user.userPhone,
    registDate: props.user.registDate,
    userRole: props.user.userRole,
  };
});

const userPassword = ref('');
const confirmPassword = ref('');

watch([userPassword, confirmPassword], () => {
  if (confirmPassword.value === '') {
    passwordMatchMessage.value = '';
  } else if (userPassword.value === confirmPassword.value) {
    passwordMatchMessage.value = '비밀번호가 일치합니다.';
    validation.value = true;
  } else {
    passwordMatchMessage.value = '비밀번호가 일치하지 않습니다.';
    validation.value = false;
  }
});

const updateUser = () => {
  if (validation.value) {
    modifyUser.value.userPassword = userPassword.value;
  } else {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  emit('updateUser', modifyUser.value);
};

const changeImage = () => {
  if (selectedImg.value) {
    emit('changeImage', selectedImg.value);
  }
};

const deleteImage = () => {
  emit('deleteImage');
};

const onFileSelected = (event) => {
  selectedImg.value = event.target.files[0];
  emit('onFileSelected', { data: selectedImg.value });
};
</script>

<template>
  <div class="container-lg" style="padding-top: 60px">
    <div class="row">
      <div class="col-md-12 col-sm-12 col-xs-12 personal-info">
        <div class="d-flex justify-content-center align-items-center mb-3 position-relative">
          <h3 class="text-center m-0">회원 정보 수정</h3>
        </div>
        <form class="form-horizontal" role="form">
          <div class="form-group row text-center border-wrapper">
            <div class="profile-header-img mb-3 mx-auto">
              <img
                :src="`data:image/png;base64, ${props.imageSrc}`"
                alt="Profile Image"
                class="rounded-circle img-fluid"
                style="width: 150px; height: 150px"
              />
            </div>
            <div class="file-input-wrapper mx-auto">
              <div>
                <label class="form-label">이미지 변경</label><br />
                <label class="form-label">(이미지 사진만 변경 가능)</label>
              </div>
              <div class="custom-file-upload">
                <input type="file" id="file-upload" @change="onFileSelected" />
                <label for="file-upload" class="file-upload-label">파일 선택</label>
              </div>
            </div>
            <div class="d-flex justify-content-center">
              <button class="btn btn-primary mr-2" type="button" @click="changeImage">저장</button>
              <button class="btn btn-danger" type="button" @click="deleteImage">삭제</button>
            </div>
          </div>
          <div class="form-group row">
            <label class="col-lg-3 control-label">아이디</label>
            <div class="col-lg-8">
              <input class="form-control" v-model="modifyUser.userId" type="text" readonly />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-lg-3 control-label">이메일</label>
            <div class="col-lg-8">
              <input class="form-control" v-model="modifyUser.userEmail" type="text" />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-lg-3 control-label">주소</label>
            <div class="col-lg-8">
              <input class="form-control" v-model="modifyUser.userAddress" type="text" />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-lg-3 control-label">전화번호</label>
            <div class="col-lg-8">
              <input class="form-control" v-model="modifyUser.userPhone" type="text" />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-lg-3 control-label">가입일</label>
            <div class="col-lg-8">
              <input class="form-control" v-model="modifyUser.registDate" type="text" readonly />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-md-3 control-label">비밀번호</label>
            <div class="col-md-8">
              <input class="form-control" v-model="userPassword" type="password" />
            </div>
          </div>
          <div class="form-group row">
            <label class="col-md-3 control-label">비밀번호 확인</label>
            <div class="col-md-8">
              <input class="form-control" v-model="confirmPassword" type="password" />
            </div>
          </div>
          <div
            class="password-message"
            :class="{
              'text-success': passwordMatchMessage === '비밀번호가 일치합니다.',
              'text-danger': passwordMatchMessage === '비밀번호가 일치하지 않습니다.',
            }"
          >
            {{ passwordMatchMessage }}
          </div>
          <div class="form-group row">
            <div class="col-md-8 offset-md-3">
              <input class="btn btn-primary" value="수정" type="button" @click="updateUser" />
              <input class="btn btn-secondary" value="취소" type="reset" />
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
}

.password-message {
  font-size: 0.9rem;
  margin-top: -10px;
  margin-bottom: 10px;
}

.text-success {
  color: green;
}

.text-danger {
  color: red;
}

.personal-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.form-horizontal {
  margin-top: 20px;
}

.alert {
  margin-bottom: 20px;
}

h3 {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.btn-primary {
  background-color: #8cc0de;
  border-color: #8cc0de;
  color: white;
}

.btn-primary:hover {
  background-color: #5bbaf1;
  border-color: #5bbaf1;
}

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
  border-color: #bd2130;
}

.btn-secondary {
  background-color: #6c757d;
  border-color: #6c757d;
  color: white;
}

.profile-header-img {
  position: relative;
}

.profile-header-img img {
  border: 3px solid #ffffff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.btn-block {
  width: auto;
}

.d-flex .btn {
  width: auto;
}

.file-input-wrapper {
  display: inline-block;
  text-align: center;
}

.custom-file-upload {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #8cc0de;
  border-radius: 5px;
  width: 150px;
  height: 40px;
  color: #fff;
  background-color: #8cc0de;
  cursor: pointer;
  margin-top: 10px;
}

.custom-file-upload:hover {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #8cc0de;
  border-radius: 5px;
  width: 150px;
  height: 40px;
  color: #fff;
  background-color: #8cc0de;
  cursor: pointer;
  margin-top: 10px;
}

.custom-file-upload input[type='file'] {
  display: none;
}

.custom-file-upload .file-upload-label {
  margin: 0;
  color: #fff;
  font-weight: bold;
}

.border-wrapper {
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
}

.image-info {
  width: 20px;
  height: 20px;
  margin-left: 10px;
  cursor: pointer;
}

.info-container {
  position: absolute;
  margin-top: 130px;
  margin-left: 700px;
  width: 500px;
  height: 200px;
}

.info-popup {
  position: absolute;
  top: 25px;
  left: 45%;
  transform: translateX(-50%);
  width: 320px;
  height: 70px;
  border: 1px solid black;
  padding: 10px;
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
  white-space: nowrap;
  z-index: 10;
}

.info-container:hover .info-popup {
  opacity: 1;
}
</style>
