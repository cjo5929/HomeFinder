<script setup>
import { defineProps, defineEmits, ref } from "vue";

defineProps({
  imageSrc: String,
  user: Object,
});
const emit = defineEmits(["changeImage", "deleteImage", "onFileSelected"]);

const selectedImg = ref(null);

const changeImage = () => {
  emit("changeImage");
};
const deleteImage = () => {
  emit("deleteImage");
};
const onFileSelected = (event) => {
  selectedImg.value = event.target.files[0];
  console.log(selectedImg.value);
  emit("onFileSelected", { data: selectedImg.value });
};
</script>

<template>
  <div class="container-lg mt-5">
    <div class="row">
      <div class="row text-center">
        <div class="card profile-card">
          <div class="card-body">
            <h5 class="card-title">MyPage</h5>
            <div class="profile-header">
              <img
                :src="`data:image/png;base64, ${imageSrc}`"
                alt="Profile Image"
                class="profile-image mb-3"
              />
              <h3>안녕하세요, {{ user.userId }}</h3>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="card">
          <div class="card-header">
            <ul class="nav nav-tabs card-header-tabs">
              <li class="nav-item">
                <a class="nav-link active" href="#info" data-toggle="tab">회원 정보</a>
              </li>
            </ul>
          </div>
          <div class="card-body">
            <div class="tab-content">
              <div class="tab-pane fade show active" id="info">
                <table class="table table-hover">
                  <tbody>
                    <tr>
                      <th>아이디</th>
                      <td>{{ user.userId }}</td>
                    </tr>
                    <tr>
                      <th>이메일</th>
                      <td>{{ user.userEmail }}</td>
                    </tr>
                    <tr>
                      <th>전화번호</th>
                      <td>{{ user.userPhone }}</td>
                    </tr>
                    <tr>
                      <th>주소</th>
                      <td>{{ user.userAddress }}</td>
                    </tr>
                    <tr>
                      <th>회원가입일</th>
                      <td>{{ user.registDate }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.profile-header {
  background-color: #f8f9fa;
  padding: 20px;
}

.profile-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ffffff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.card {
  margin-top: 20px;
}

.profile-card {
  height: 350px; /* Adjust this value as needed */
  overflow: hidden;
}

.card-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.table th,
.table td {
  vertical-align: middle;
}

.nav-tabs .nav-link {
  color: #8cc0de;
  font-weight: 600;
}

.nav-tabs .nav-link.active {
  color: #5bbaf1;
}

.btn-block {
  width: 100%;
}
</style>
