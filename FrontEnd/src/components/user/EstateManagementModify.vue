<template>
    <div class="container-xl mt-5">
      <div class="row justify-content-center">
        <div class="col-12 text-center">
          <img :src="`data:image/png;base64, ${dataObj.base64}`" class="img-fluid mb-3 detail-image" />
          <input type="file" class="form-control-file mb-3" @change="onFileSelected" />
          <div class="section-title">아파트이름</div>
          <h2><input type="text" v-model="detailObject.name" class="form-control" readonly/></h2>
        </div>
      </div>
      <div class="row price-info">
        <div class="col-12">
          <div class="section-title">가격정보</div>
          <div class="card mb-3 price-card">
            <div class="card-body">
              <div class="row detail-item">
                <div class="col-4">
                  <label>구분</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="type" class="form-control" readonly/>
                </div>
              </div>
              <div class="row detail-item">
                <div class="col-4">
                  <label>가격</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.price" class="form-control" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row detail-info">
        <div class="col-12">
          <div class="section-title">상세정보</div>
          <div class="card detail-card">
            <div class="card-body">
              <div class="row detail-item">
                <div class="col-4">
                  <label>주소</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.address" class="form-control" readonly/>
                </div>
              </div>               
              <div class="row detail-item">
                <div class="col-4">
                  <label>방 개수</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.room" class="form-control" />
                </div>
              </div>
              <div class="row detail-item">
                <div class="col-4">
                  <label>해당층</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.floor" class="form-control" />
                </div>
              </div>
              <div class="row detail-item">
                <div class="col-4">
                  <label>면적</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.area" class="form-control" />
                </div>
              </div>
              <div class="row detail-item">
                <div class="col-4">
                  <label>욕실 수</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.bath" class="form-control" />
                </div>
              </div>
              <div class="row detail-item">
                <label class="col-4">난방종류</label>
                <div class="col-8 d-flex align-items-center">
                  <div class="form-check me-3">
                    <input class="form-check-input" type="radio" value="전체 관리" v-model="detailObject.heater">
                    <label class="form-check-label">전체 관리</label>
                  </div>
                  <div class="form-check">
                    <input class="form-check-input" type="radio" value="개별 난방" v-model="detailObject.heater">
                    <label class="form-check-label">개별 난방</label>
                  </div>
                </div>
              </div>
              <div class="row detail-item">
                <label class="col-4">현관입구</label>
                <div class="col-8 d-flex align-items-center">
                  <div class="form-check me-3">
                    <input class="form-check-input" type="radio" value="복합식" v-model="detailObject.entrance">
                    <label class="form-check-label">복합식</label>
                  </div>
                  <div class="form-check">
                    <input class="form-check-input" type="radio" value="계단식" v-model="detailObject.entrance">
                    <label class="form-check-label">계단식</label>
                  </div>
                </div>
              </div>
              <div class="row detail-item">
                <label class="col-4 mt-1">입주가능일</label>
                <div class="col-8">
                  <input type="date" class="form-control" v-model="detailObject.move_in">
                </div>
              </div>
              <div class="row detail-item">
                <div class="col-4">
                  <label>최초등록일</label>
                </div>
                <div class="col-8">
                  <input type="text" v-model="detailObject.regist_date" class="form-control" readonly />
                </div>
              </div>
              <div class="row detail-item mt-2">
                <div class="col-4">
                  <label>상세 설명</label>
                </div>
                <div class="col-8">
                  <textarea class="form-control" v-model="detailObject.info" placeholder="상세 설명" rows="3"></textarea>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row justify-content-center mt-4">
        <div class="col-3"></div>
        <button class="col btn btn-primary btn-lg mx-2" v-if="detailObject.user_id === userInfo.userId" @click="detailModify">수정</button>
        <div class="col-3"></div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps, onMounted, ref } from 'vue';
  import { useUserStore } from "@/stores/userStore.js";
  import { storeToRefs } from 'pinia';
  import { useRouter } from "vue-router";
  import {updateItem} from "@/api/estate.js";
  
  const router = useRouter();
  const store = useUserStore();
  const { userInfo } = storeToRefs(store);
  const { dataObj } = history.state;
  console.log(dataObj);
  const detailObject = ref({});
  const type = ref("");
  const selectedFile = ref(null);
  
  onMounted(() => {
    if (dataObj.type === "trading") {
      type.value = "매매";
    } else if (dataObj.type === "monthly") {
      type.value = "월세";
    } else {
      type.value = "전세";
    }
    detailObject.value = dataObj;
    console.log("detailObject : ", detailObject.value);
  })
  
  const onFileSelected = (event) => {
    selectedFile.value = event.target.files[0];
    console.log("Selected file:", selectedFile.value);
  };
  
  const detailModify = () => {
    const formData = new FormData();
    formData.append("item_no", detailObject.value.item_no);
    formData.append("file_no", detailObject.value.file_no);
    formData.append("address", detailObject.value.address);
    formData.append("area", detailObject.value.area);
    formData.append("bath", detailObject.value.bath);
    formData.append("floor", detailObject.value.floor);
    formData.append("heater", detailObject.value.heater);
    formData.append("info", detailObject.value.info);
    formData.append("move_in", detailObject.value.move_in);
    formData.append("name", detailObject.value.name);
    formData.append("price", detailObject.value.price);
    formData.append("room", detailObject.value.room);
    formData.append("type", detailObject.value.type);
    formData.append("entrance", detailObject.value.entrance);
    if (selectedFile.value) {
        formData.append("image", selectedFile.value);
    }
    console.log(detailObject.value);
    console.log(formData.get("item_no"));
    console.log(formData.get("file_no"));
    updateItem(formData, (response)=>{
      alert("등록 물품 수정에 성공했습니다.");
      router.push({name:"management"});
    }, (error)=>{
  
    })
  }
  </script>
  
  <style scoped>
  .detail-card {
    height: 560px;
  }
  
  .price-card {
    height: 140px;
  }
  
  .price-info {
    margin-top: 20px;
  }
  
  .detail-info {
    margin-top: 20px;
  }
  
  .container-xl {
    background-color: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
  }
  
  .detail-image {
    max-width: 300px; /* 이미지 크기를 줄임 */
    height: auto;
    display: block;
    margin: 0 auto;
  }
  
  .section-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin-top: 20px;
    margin-bottom: 10px;
  }
  
  .detail-item {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
  }
  
  .detail-item label {
    font-weight: bold;
  }
  
  .card {
    border: 1px solid #ddd;
  }
  
  .card-body {
    padding: 10px;
  }
  
  .text-center {
    text-align: center;
  }
  
  .btn-lg {
    padding: 10px 20px;
    font-size: 1.2rem;
  }
  
  .form-check {
    display: flex;
    align-items: center;
  }
  </style>
  