<script setup>
import { defineProps, defineEmits, ref, onMounted } from "vue";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";

const props = defineProps({
  imageSrc: String,
  user: Object,
});
const emit = defineEmits(["changeImage", "deleteImage", "onFileSelected", "deleteUser"]);

const store = useUserStore();
const { userInfo } = storeToRefs(store);
const selectedImg = ref(null);
const checked = ref(false);
const deleteUser = ref({ ...props.user });

const timeDiff = ref("");

onMounted(() => {
  timeDiff.value = getTimeDifference(deleteUser.value.registDate);
  console.log("userInfo = ", userInfo);
});

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

const deleteUserFunc = () => {
  if (!checked.value) {
    alert("내용 동의를 먼저 해주세요.");
    return;
  }
  emit("deleteUser", deleteUser.value.userPassword);
};

function getTimeDifference(dateString) {
  const targetTime = new Date(dateString);
  const currentTime = new Date();
  console.log("가입일 = ", targetTime, " 지금시간 = ", currentTime);

  const timeDiff = currentTime.getTime() - targetTime.getTime();

  const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((timeDiff % (1000 * 60)) / 1000);

  return `${days}일 ${hours}시간 ${minutes}분 ${seconds}초`;
}
</script>

<template>
  <div class="container-lg mt-5">
    <div class="row">
      <div class="col-12">
        <div class="card mb-3 profile-card">
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
        <div class="card">
          <div class="card-body">
            <h3 class="card-title mb-4">회원탈퇴</h3>
            <p class="card-text">탈퇴하기 전에 유의사항을 꼭 확인해주세요.</p>
            <ul class="mb-4">
              <li>탈퇴 후에는 모든 개인 정보와 서비스 이용 기록이 삭제됩니다.</li>
              <li>탈퇴 후에는 동일한 아이디로 재가입이 불가능합니다.</li>
            </ul>
            <p> 현재 저희와 {{ timeDiff }}만큼 함께 하셨습니다.</p>
            <p>정말 탈퇴하시겠습니까?</p>
            <form>
              <div class="mb-3">
                <label for="password" class="form-label">비밀번호</label>
                <input type="password" class="form-control" v-model="deleteUser.userPassword" id="password" placeholder="비밀번호를 입력하세요">
              </div>
              <div class="form-check mb-4">
                <input class="form-check-input" type="checkbox" id="agreement" v-model="checked">
                <label class="form-check-label" for="agreement">
                  위 내용에 동의합니다.
                </label>
              </div>
              <div class="d-grid gap-2">
                <button type="submit" class="btn btn-danger" @click.prevent="deleteUserFunc">탈퇴하기</button>
              </div>
            </form>
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
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
}

.card {
  margin-top: 20px;
}


.card-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.table th, .table td {
  vertical-align: middle;
}

.nav-tabs .nav-link {
  color: #495057;
  font-weight: 600;
}

.nav-tabs .nav-link.active {
  color: #007bff;
}

.btn-block {
  width: 100%;
}

.profile-card{
  height : 350px;
}
</style>