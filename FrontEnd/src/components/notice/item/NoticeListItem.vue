<template>
  <tr @click="moveDetail(notice.id)">
    <td>{{ notice.id }}</td>
    <td>{{ notice.title }}</td>
    <td>{{ notice.writer }}</td>
    <td>{{ notice.created_at }}</td>
  </tr>
</template>

<script setup>
import { useRouter } from 'vue-router';

defineProps({
  notice: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

const moveDetail = (noticeId) => {
  router.push({ name: 'NoticeDetail', params: { id: noticeId } });
};
</script>

<style scoped>
tr {
  cursor: pointer;
  transition: background-color 0.3s ease;
  border-bottom: 1px solid #e0e0e0;
}

tr:hover {
  background-color: #f1f1f1;
}

td {
  padding: 12px 15px;
}
</style>
