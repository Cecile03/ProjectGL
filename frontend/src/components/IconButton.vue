<script lang="ts" setup>
import router from '@/router';

import { useRoute } from 'vue-router';
import type { Component } from 'vue';

type Props = {
  icon: Component;
  isProfile?: boolean;
  redirectTo: string;
  badge?: number;
};
const props = defineProps<Props>();
const route = useRoute();

const handleClick = () => {
  router.push(props.redirectTo);
};
</script>

<template>
  <div
    :class="['icon-button', props.isProfile ? 'icon-button-profile' : '']"
    @click="handleClick">
    <n-icon :component="props.icon" :size="22" />
    <p class="badge" v-if="props.badge && props.badge > 0">{{ props.badge }}</p>
    <div :class="[route.path === props.redirectTo ? 'selected' : '']"></div>
  </div>
</template>

<style>
.icon-button {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
  width: 40px;
  height: 40px;
  border-radius: 100%;
  cursor: pointer;
  transition: all 0.2s;
}
.icon-button:hover {
  background-color: var(--onBackground);
}
.icon-button .badge {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--redColor);
  top: 0px;
  right: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  font-size: 0.6rem;
  color: var(--onBackground);
}
.icon-button-profile {
  background-color: var(--onBackground);
}
.icon-button .selected {
  position: absolute;
  bottom: 2px;
  height: 4px;
  width: 12px;
  border-radius: 10px;
  background-color: var(--primaryColor);
  animation: 0.2s selectedAnim;
}
@keyframes selectedAnim {
  from {
    width: 0;
  }

  to {
    width: 12px;
  }
}
</style>
