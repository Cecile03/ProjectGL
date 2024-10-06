<template>
  <div class="error-page">
    <div class="error-content">
      <n-icon  :size="80" :component="props.icon" />
      <p class="error-subtitle">{{ props.title }} </p>
      <p class="error-message">
          {{ props.subtitle }} 
      </p>
      <n-button  @click="navigate">{{ props.boutonString }}</n-button>
    </div>
    <div class="error-illustration">
      <img class="illustration-image" src="@/assets/img/astronaute.png" alt="Error Illustration" />
    </div>
  </div>
</template>
  
<script setup lang='ts'>
import { useRouter } from 'vue-router';
import type { Component } from 'vue';

type Props = {
  icon: Component;
  title: string;
  subtitle: string;
  path?: string;
  boutonString?: string;
}

const props = defineProps<Props>();

const router = useRouter();

const navigate = () => {
  router.push(props.path || '/');
};

</script>

<style scoped>
.error-page {
  margin-top: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.error-content {
  flex: 1;
}

.error-title {
  font-size: 12rem;
  font-weight: bold;
  color: #FFF;
}

.error-subtitle {
  font-size: 3rem;
  color: var(--textColor);
  margin-top: 1rem;
}

.error-message {
  color: var(--textColor);
  margin: 1rem 0;
}

.error-illustration {
  flex:1;
}

.illustration-image {
  width: 400px; /* Adjust the size of the image */
  height: auto;
  position: relative; /* Position the image absolutely... */
  right: 0; /* ...and align it to the right */
}
</style>
  