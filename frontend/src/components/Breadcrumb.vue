<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const breadcrumbs = computed(() => {
  const matched = route.matched;
  const crumbs = matched.map((route) => ({
    path: route.path,
    name: route.name,
  }));
  return crumbs;
});
</script>

<template>
  <div class="breadcrumb">
    <n-breadcrumb>
      <n-breadcrumb-item
        v-for="(crumb, index) in breadcrumbs"
        :key="crumb.path">
        <RouterLink
          :class="{ 'nav-link-selected': index === breadcrumbs.length - 1 }"
          :to="crumb.path"
          >{{ crumb.name }}</RouterLink
        >
      </n-breadcrumb-item>
    </n-breadcrumb>
  </div>
</template>

<style scoped>
.breadcrumb {
  margin: 10px;
}
.nav-link-selected {
  color: var(--primaryColor);
}
</style>
