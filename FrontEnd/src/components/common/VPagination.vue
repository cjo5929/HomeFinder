<script setup>
import { computed } from 'vue';

const props = defineProps({
  currentPage: Number,
  totalPage: Number,
});

const emit = defineEmits(['pageChange']);

const navigationSize = 10;

const startPage = computed(() => {
  return Math.floor((props.currentPage - 1) / navigationSize) * navigationSize + 1;
});

const endPage = computed(() => {
  let lastPage = Math.floor((props.currentPage - 1) / navigationSize) * navigationSize + navigationSize;
  return props.totalPage < lastPage ? props.totalPage : lastPage;
});

function range(start, end) {
  const list = [];
  for (let i = start; i <= end; i++) list.push(i);
  return list;
}

function onPageChange(pg) {
  if (pg >= 1 && pg <= props.totalPage) {
    emit('pageChange', pg);
  }
}
</script>

<template>
  <div class="row">
    <ul class="pagination justify-content-center">
      <li class="page-item" :class="{ disabled: props.currentPage === 1 }">
        <a class="page-link" @click="onPageChange(1)">최신</a>
      </li>
      <li class="page-item" :class="{ disabled: startPage === 1 }">
        <a class="page-link" @click="onPageChange(startPage === 1 ? 1 : startPage - 1)">이전</a>
      </li>

      <template v-for="pg in range(startPage, endPage)" :key="pg">
        <li :class="props.currentPage === pg ? 'page-item active' : 'page-item'">
          <a class="page-link" @click="onPageChange(pg)">{{ pg }}</a>
        </li>
      </template>
      <li class="page-item" :class="{ disabled: endPage === props.totalPage }">
        <a class="page-link" @click="onPageChange(endPage + 1)">다음</a>
      </li>
      <li class="page-item" :class="{ disabled: props.currentPage == props.totalPage }">
        <a class="page-link" @click="onPageChange(props.totalPage)">마지막</a>
      </li>
    </ul>
  </div>
</template>
<style scoped>
a {
  cursor: pointer;
}

.pagination {
  display: flex;
  list-style: none;
  padding: 0;
}

.page-item {
  margin: 0 5px;
}

.page-link {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ccc;
  color: #8cc0de;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.page-link:hover {
  background-color: #8cc0de;
  color: white;
}

.page-item.active .page-link {
  background-color: #8cc0de;
  color: white;
  border-color: #8cc0de;
}

.page-item.disabled .page-link {
  cursor: not-allowed;
  color: #ccc;
}
</style>
