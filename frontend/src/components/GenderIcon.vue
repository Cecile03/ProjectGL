<template>
  <div :class="['gender-icon', props.gender === 'M' ? 'male' : 'female']">
    <n-icon :component="props.gender==='M' ?ManOutlined : WomanOutlined" />
  </div>
</template>

<script setup lang="ts">
import ManOutlined from '@vicons/antd/ManOutlined';
import WomanOutlined from '@vicons/antd/WomanOutlined';
import { NIcon } from 'naive-ui';

const props = defineProps<{
  gender: string;
}>();

</script>

<style scoped>
.gender-icon {
  display: inline-block;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  padding: 4px;
  color: var(--onBackground);
}

.male {
  background-color: var(--maleColor);
}

.female {
  background-color: var(--femaleColor);
}
</style>
