<script setup>
import NoticeFormItem from './item/NoticeFormItem.vue';
</script>

<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <h2 class="my-3 py-3 shadow-sm bg-light text-center">
          <mark class="sky">공지사항 작성</mark>
        </h2>
      </div>
      <div class="col-lg-10 text-start">
        <NoticeFormItem type="regist" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  margin-top: 20px;
  padding: 20px;
  max-width: 800px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #333;
}

.sky {
  background-color: #e0f7fa;
}

.shadow-sm {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
