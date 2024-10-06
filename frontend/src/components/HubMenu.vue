<script setup lang="ts">
import { Info24Regular } from '@vicons/fluent';
import Navigator, {
  type Navigator as NavigatorType,
} from '@/components/Navigator.vue';

type Props = {
  navigators: NavigatorType[];
};
defineProps<Props>();
</script>

<template>
  <div class="info">
    <n-icon class="icon" :size="18" :component="Info24Regular"></n-icon>
    <p>
      Cliquez sur l'une des cartes suivantes pour vous rendre à la page
      correspondante.
    </p>
  </div>
  <div class="navigator-container">
    <Navigator
      v-for="navigator in navigators"
      :key="navigator.title"
      :navigator="navigator" />
  </div>
</template>

<style scoped>
.info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  margin-left: 25px; /* According to navigator width */
}
.info .icon {
  margin-right: 5px;
}

.navigator-container {
  display: flex;
  flex-wrap: wrap;
}
</style>
