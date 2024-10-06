<script setup lang="ts">
import { LottieAnimation } from 'lottie-web-vue';
import LoadingJSON from '../assets/lottie/3dots_blue.json';
</script>
<template>
  <div class="loading-absolute">
    <LottieAnimation
      :animation-data="LoadingJSON"
      :auto-play="true"
      :loop="true"
      :speed="2" />
  </div>
</template>

<style scoped>
.loading-absolute {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translateX(-50%) translateY(-50%);
  width: 300px;
}
</style>
