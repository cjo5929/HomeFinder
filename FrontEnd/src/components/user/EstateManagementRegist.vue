<template>
  <div class="container-fluid mt-5">
    <div class="row mb-4">
      <h3 class="section-title">물품 등록 정보 기입</h3>
    </div>
    <div class="row mb-4 align-items-center">
      <button class="btn btn-primary col-3" @click="search">우편번호 찾기</button>
      <input type="text" id="dongCode" v-model="dongCode" class="form-control col-7 ml-2" placeholder="동 코드" />
    </div>
    <div class="row mb-4">
      <div class="col">
        <button class="btn btn-secondary" @click="showModal = true">아파트 목록 보기</button>
      </div>
    </div>
    <div class="card card-detail">
      <div class="card-body">
        <div class="row">
          <h3 class="section-title">기본 정보</h3>
        </div>
        <div class="row mb-4">
          <div class="col-3">
            <h3 class="section-title">구분</h3>
          </div>
          <div class="col-9">
            <select v-model="selectedRadio" class="form-control">
              <option value="trading">매매</option>
              <option value="charter">전세</option>
              <option value="monthly">월세</option>
            </select>
          </div>
        </div>
        <div class="row mb-2" v-for="(label, key) in inputFields" :key="key">
          <div class="col-3">
            <label :for="key">{{ label }}</label>
          </div>
          <div class="col-9">
            <input
              type="text"
              :id="key"
              :placeholder="label"
              class="form-control"
              v-model="selectedItem[key]"
              :readonly="key === 'name'"
            />
          </div>
        </div>
        <div class="row mb-2" v-show="selectedRadio === 'monthly'">
          <div class="col-3">
            <label for="deposit">보증금</label>
          </div>
          <div class="col-9">
            <input type="text" id="deposit" class="form-control" placeholder="보증금" v-model="selectedItem.deposit" />
          </div>
        </div>
        <div class="row mb-2">
          <div class="col-3">
            <label for="move_in">입주가능일</label>
          </div>
          <div class="col-9">
            <input type="date" id="move_in" class="form-control" v-model="selectedItem.move_in" />
          </div>
        </div>
        <div class="row mb-2">
          <div class="col-3">
            <label>난방종류</label>
          </div>
          <div class="col-9">
            <div class="row">
              <div class="col">
                <input class="form-check-input" type="radio" value="전체 관리" v-model="selectedItem.heater" />
                <label class="form-check-label">전체 관리</label>
              </div>
              <div class="col">
                <input class="form-check-input" type="radio" value="개별 난방" v-model="selectedItem.heater" />
                <label class="form-check-label">개별 난방</label>
              </div>
            </div>
          </div>
        </div>
        <div class="row mb-2">
          <div class="col-3">
            <label>현관입구</label>
          </div>
          <div class="col-9">
            <div class="row">
              <div class="col">
                <input class="form-check-input" type="radio" value="복합식" v-model="selectedItem.entrance" />
                <label class="form-check-label">복합식</label>
              </div>
              <div class="col">
                <input class="form-check-input" type="radio" value="계단식" v-model="selectedItem.entrance" />
                <label class="form-check-label">계단식</label>
              </div>
            </div>
          </div>
        </div>
        <div class="row mb-2">
          <div class="col-3">
            <label for="info">상세 설명</label>
          </div>
          <div class="col-9">
            <textarea
              id="info"
              class="form-control"
              v-model="selectedItem.info"
              placeholder="상세 설명"
              rows="5"
            ></textarea>
          </div>
        </div>
        <div class="row">
          <div class="col">
            <h3 class="section-title">물품 사진 등록</h3>
            <div class="custom-file">
              <input type="file" class="custom-file-input" id="customFile" @change="onFileSelected" />
              <label class="custom-file-label" for="customFile">파일 선택</label>
            </div>
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <button class="btn btn-success btn-block" @click="uploadItem">물품 등록</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container" style="max-height: 650px">
          <div class="modal-header">
            <h5 class="modal-title">아파트 목록</h5>
            <button type="button" class="close" @click="showModal = false">&times;</button>
          </div>
          <div class="modal-body" style="overflow-y: auto; max-height: 500px">
            <div v-for="(element, index) in apartList" :key="index" class="modal-list-item mb-2 p-2 border rounded">
              <div class="d-flex justify-content-between align-items-center">
                <span>{{ element.name }}</span>
                <button class="btn btn-secondary btn-sm" @click="select(element)">선택</button>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showModal = false">닫기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore.js';
import { useEstateStore } from '@/stores/estateStore.js';
import { getApartListByDongCode } from '@/api/map.js';
import { storeToRefs } from 'pinia';

export default {
  name: 'estate',
  setup() {
    const dongCode = ref('');
    const apartList = ref([]);
    const selectedItem = ref({});
    const selectedRadio = ref('');
    const fileUpload = ref(null);
    const showModal = ref(false);
    const store = useUserStore();
    const estateStore = useEstateStore();
    const { userInfo } = storeToRefs(store);
    const { storeUploadItem } = estateStore;

    const select = (element) => {
      selectedItem.value = {
        ...selectedItem.value,
        ...element,
      };
      console.log('selected = ', selectedItem.value);
      apartList.value = [];
      showModal.value = false;
    };

    const search = () => {
      new window.daum.Postcode({
        oncomplete: (data) => {
          console.log(data);
          selectedItem.value.address = data.address;
          dongCode.value = data.bcode;
        },
      }).open();
    };

    watch(dongCode, (newValue, oldValue) => {
      console.log('new = ', newValue);
      getApartListByDongCode(
        newValue,
        (response) => {
          console.log('response = ', response);
          apartList.value = response.data.list;
        },
        (error) => {
          console.log('error = ', error);
        }
      );
    });

    let formData = null;
    const onFileSelected = () => {
      fileUpload.value = event.target.files[0];
      console.log(fileUpload.value);
      formData = new FormData();
      formData.append('image', fileUpload.value);
      formData.append('userId', userInfo.value.userId);
    };

    const uploadItem = () => {
      selectedItem.value.type = selectedRadio.value;
      if (!selectedItem.value.type) {
        alert('구분을 선택해주세요.');
        return;
      }
      const sendFormData = new FormData();
      selectedItem.value.user_id = userInfo.value.userId;
      selectedItem.value.type = selectedRadio.value;
      if (fileUpload.value) {
        sendFormData.append('image', fileUpload.value);
      }
      sendFormData.append('dto', JSON.stringify(selectedItem.value));
      storeUploadItem(sendFormData);
    };

    const inputFields = {
      name: '아파트 이름',
      address: '주소',
      floor: '층',
      area: '면적',
      room: '방 수',
      bath: '욕실 수',
      price: '가격',
    };

    return {
      dongCode,
      search,
      apartList,
      select,
      selectedItem,
      selectedRadio,
      onFileSelected,
      uploadItem,
      showModal,
      inputFields,
    };
  },
};
</script>

<style scoped>
.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 10px;
}

.container-fluid {
  padding: 20px;
}

.row {
  margin-bottom: 20px;
}

textarea.form-control {
  height: 100%;
}

input[type='file'].custom-file-input {
  margin-top: 10px;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.card-body {
  padding: 20px;
}

.card-detail {
  height: 1050px;
}

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-wrapper {
  width: 500px;
}

.modal-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.modal-body {
  padding-top: 10px;
}

.modal-footer {
  padding-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.modal-list-item {
  background: #f8f9fa;
}

.custom-file-input ~ .custom-file-label::after {
  content: '파일 선택';
}
</style>
