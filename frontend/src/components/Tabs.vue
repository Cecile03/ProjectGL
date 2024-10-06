<script lang="ts" setup>
type Props = {
  tabs: string[];
  onClick: (tabIndex: number) => void;
  selectedTabIndex: number;
  disabledTabs?: number[];
  validateTabs?: number[];
};

const props = defineProps<Props>();

const isDisabled = (index: number) => {
  if (!props.disabledTabs) return false;
  return props.disabledTabs.includes(index);
};
const isValidate = (index: number) => {
  if (!props.validateTabs) return false;
  return props.validateTabs.includes(index);
};
</script>

<template>
  <div class="tabs">
    <div
      v-for="(tab, index) in tabs"
      :key="index"
      @click="isDisabled(index) ? null : onClick(index)"
      :class="[
        'tab',
        {
          active: selectedTabIndex === index,
          disabled: isDisabled(index),
        },
      ]">
      <span class="label">{{ tab }}</span>
      <i v-if="isDisabled(index)" :class="['fa-solid fa-lock', 'locked']"></i>
      <i
        v-if="isValidate(index)"
        :class="['fa-solid fa-circle-check', 'validate']"></i>
    </div>
  </div>
</template>

<style scoped>
.tabs {
  width: 100%;
  display: flex;
  align-items: center;
}
.tabs .tab {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px;
  white-space: nowrap;
  cursor: pointer;
  border-top: 2px solid var(--textColor);
  background-color: var(--onBackground);
  transition: all 0.3s;
}
.tabs .tab .label {
  color: var(--textColor);
}
.tabs .active {
  background-color: var(--backgroundColor);
  border-top: 2px solid var(--borderColor);
  font-weight: 500;
}
.tabs .active + .tab {
  border-radius: 0 0 0 10px;
}
.tabs .tab:has(+ .active) {
  border-radius: 0 0 10px 0;
}
.tabs .disabled {
  cursor: not-allowed;
}
.tabs .disabled .label {
  opacity: 0.5;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.tabs .locked {
  animation: fadeIn 1s forwards;
  color: var(--textColor);
}
.tabs .validate {
  animation: fadeIn 1s forwards;

  color: var(--greenColor);
}
</style>
