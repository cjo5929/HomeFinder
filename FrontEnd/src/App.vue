<script setup>
import { RouterView } from 'vue-router';
import TheHeadingNavbar from '@/components/layout/TheHeadingNavbar.vue';
import TheFooter from '@/components/layout/TheFooter.vue';
</script>

<template>
  <div class="app-container">
    <TheHeadingNavbar></TheHeadingNavbar>
    <div class="content-container">
      <router-view></router-view>
    </div>
    <TheFooter></TheFooter>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content-container {
  flex: 1;
  height: calc(100vh - var(--nav-height) - var(--footer-height));
  overflow: hidden;
}
</style>
