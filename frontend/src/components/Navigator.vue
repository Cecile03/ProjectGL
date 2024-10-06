<script setup lang="ts">
import { type Component } from 'vue';
import { ClipboardTask24Filled } from '@vicons/fluent';

export type Navigator = {
  title: string;
  description: string;
  icon: Component;
  redirectTo: string;
  validated?: boolean;
};
type Props = {
  navigator: Navigator;
};
defineProps<Props>();
</script>

<template>
  <RouterLink class="navigator" :to="navigator.redirectTo">
    <div class="banner">
      <n-icon class="icon" :size="24" :component="navigator.icon"></n-icon
      ><n-icon
        v-if="navigator.validated"
        class="validated"
        :size="24"
        :component="ClipboardTask24Filled"></n-icon>
    </div>
    <div class="wrapper">
      <p class="name">{{ navigator.title }}</p>
      <p class="description">{{ navigator.description }}</p>
    </div>
  </RouterLink>
</template>

<style scoped>
.navigator {
  width: 170px;
  height: 180px;
  margin: 25px;
  margin-top: 0;
  border: 0.1px solid var(--borderColor);
  border-radius: 20px;
  box-shadow: 0px 0px 10px 0px #eaeaeac2;
  cursor: pointer;
  overflow: hidden;
  transition: 0.3s;
}
.navigator:hover {
  box-shadow: 0px 0px 10px 0px #dbdbdb;
}

.navigator .banner {
  position: relative;
  height: 25%;
  background-color: var(--onBackground);
}
.navigator .banner .icon {
  position: absolute;
  bottom: -10px;
  left: 10%;
  color: var(--primaryColor);
}
.navigator .banner .validated {
  position: absolute;
  bottom: -10px;
  right: 10%;
  color: var(--greenColor);
}
.navigator .wrapper {
  padding: 0 15px;
  padding-top: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.navigator .name {
  text-transform: uppercase;
  font-weight: 600;
  color: var(--secondaryColor);
}
.navigator .description {
  font-size: 0.75rem;
  margin-top: 6px;
  color: var(--textColor);
}
</style>
