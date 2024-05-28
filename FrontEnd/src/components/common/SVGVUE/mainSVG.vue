<template>

</template>

<script setup lang="ts">
import { defineProps } from "vue";

defineProps({
  size: { type: String, required: true },
  color: { type: String, default: "#5AB2FF" },
});
</script>

<style scoped></style>
