<script setup>

</script>

<template>
  <div class="sidebar-container mt-4">
    <div class="section-title">매물 관리</div>
    <ul class="list-group mt-2">
      <li class="list-group-item">
        <RouterLink :to="{name:'management'}" class="custom-link">매물 관리</RouterLink>
      </li>
      <li class="list-group-item">
        <RouterLink :to="{name:'managementRegist'}" class="custom-link">매물 등록</RouterLink>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.sidebar-container {
  height: calc(100vh - 8rem); 
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 1.2rem;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 1px solid #dee2e6;
}

.list-group-item {
  font-size: 1.1rem;
}

.custom-link {
  text-decoration: none;
  color: inherit;
  display: block;
  padding: 8px 0;
}

.custom-link:hover {
  color: #007bff;
  background-color: #f8f9fa;
}
</style>
