<script lang="ts" setup>
import { useRoute } from 'vue-router';

type Props = {
  redirectTo: string;
  name: string;
};
const props = defineProps<Props>();
const route = useRoute();

const isPathMatchRedirectTo = () => {
  const currentPath = route.path;
  const targetPath = props.redirectTo;

  // Check if current path is exactly the target path
  if (currentPath === targetPath) {
    return true;
  }

  // Check if current path starts with target path and the next character is a slash
  return (
    currentPath.startsWith(targetPath) && currentPath[targetPath.length] === '/'
  );
};
</script>

<template>
  <RouterLink
    :class="['nav-link', isPathMatchRedirectTo() ? 'nav-link-selected' : '']"
    :to="props.redirectTo"
    >{{ props.name }}</RouterLink
  >
</template>

<style scoped>
.nav-link {
  position: relative;
  font-size: 1rem;
  color: var(--textColor);
  cursor: pointer;
  transition: all 0.2s;
}
.nav-link:hover {
  color: var(--primaryColor);
}
.nav-link-selected {
  font-weight: 500;
}
.nav-link-selected::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  height: 3px;
  width: 90%;
  border-radius: 10px;
  background-color: var(--primaryColor);
  animation: 0.2s afterAnim;
}
@keyframes afterAnim {
  0% {
    width: 0;
  }

  100% {
    width: 90%;
  }
}
</style>
