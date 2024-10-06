<template>
  <div class="login-page">
    <div class="card">
      <img class="logo" :src="canopusLogo" alt="Logo" />
      <h2 class="title">Connexion</h2>
      <n-form ref="formRef" :model="formValue" :rules="rules">
        <n-form-item label="Adresse e-mail" path="email">
          <n-auto-complete
            v-model:value="formValue.email"
            :input-props="{
                              autocomplete: 'disabled'
                            }"
            :options="emailOptions"
            placeholder="prenom.nom@reseau.eseo.fr"
            @keyup.enter="handleValidateClick"
            @update:value="updateEmailOptions"
            type="email"
            clearable
          />
        </n-form-item>
        <n-form-item label="Mot de passe" path="password">
          <n-input
            v-model:value="formValue.password"
            placeholder="Votre mot de passe"
            @keyup.enter="handleValidateClick"
            type="password"
            show-password-on="mousedown" />
        </n-form-item>
        <n-form-item>
          <n-button
            class="button"
            @click="handleValidateClick"
            :loading="loading">
            Se connecter
          </n-button>
        </n-form-item>
      </n-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { authService } from '@/services/auth.service';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { type FormInst, useMessage, type FormRules } from 'naive-ui';
import { useUserStore } from '@/stores/useUserStore';
import canopusLogo from '../../assets/img/canopus_logo.png';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref<FormInst | null>(null);
const message = useMessage();
const formValue = ref({ email: '', password: '' });
const loading = ref(false);

const emailDomains = ['@reseau.eseo.fr', '@eseo.fr'];

const emailOptions = ref<string[]>([]);

const updateEmailOptions = (value: string) => {
  const [localPart, domainPart] = value.split('@');
  emailOptions.value = emailDomains
    .filter(domain => !domainPart || domain.includes(domainPart))
    .map(domain => localPart + domain);
};

const rules: FormRules = {
  email: {
    type: 'email',
    required: true,
    message: 'Veuillez saisir votre email ou un email valide',
    trigger: 'blur',
  },
  password: {
    required: true,
    message: 'Veuillez saisir votre mot de passe',
    trigger: 'blur',
  },
};

const handleValidateClick = (e: MouseEvent) => {
  e.preventDefault();
  formRef.value?.validate((errors) => {
    if (!errors) {
      login();
    }
  });
};

const login = async () => {
  loading.value = true;

  const token = await authService.login(
    formValue.value.email,
    formValue.value.password
  );

  if (token) {
    userStore.loadUser().finally(() => {
      loading.value = false;
      router.push('/');
    });
  } else {
    loading.value = false;
    console.log('Erreur de connexion.');
    message.error("L'email ou le mot de passe sont incorrect.");
  }
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 85vh;
}

.card {
  display: flex;
  flex-direction: column;
  max-width: 450px;
  max-height: 500px;
  width: 100%;
  height: 100%;
  padding: 50px;
  background-color: #ffffff;
  border: 1px solid var(--borderColor);
  border-radius: 10px;
  box-shadow: 4px 6px 13px 4px rgb(0 0 0 / 12%);
}

.logo {
  display: block;
  margin: 0 auto 20px;
  width: 60px; /* Ajustez la taille de votre logo */
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 1.8rem;
  font-weight: 500;
}

.form-group {
  margin-bottom: 30px;
}

.label {
  display: block;
  margin-bottom: 5px;
  font-size: 0.8rem;
  font-weight: 400;
  color: var(--textColor);
}

.button {
  margin: 0 auto;
}
</style>
