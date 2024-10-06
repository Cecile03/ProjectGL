<script setup lang="ts">
import { useUserStore } from '@/stores/useUserStore';
import { formatUserRoles } from '@/utils/utils';
import { PersonOutline } from '@vicons/ionicons5';

const user = useUserStore().getUser();
</script>

<template>
  <div class="profile">
    <div class="content">
      <div class="me">
        <div class="icon-container">
          <n-icon :component="PersonOutline" :size="24" />
        </div>
        <p class="section">Détails de l'utilisateur</p>
        <div class="personnal-info">
          <div class="names">
            <div class="row">
              <p class="label">Nom</p>
              <p class="value">{{ user?.lastName }}</p>
            </div>
            <div class="row">
              <p class="label">Prénom</p>
              <p class="value">{{ user?.firstName }}</p>
            </div>
          </div>
          <div class="row">
            <p class="label">Adresse e-mail</p>
            <p class="value">{{ user?.email }}</p>
          </div>
          <div class="row">
            <p class="label">Vous êtes reconnu en tant que</p>
            <p class="value">{{ formatUserRoles(user?.roles) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content {
  padding-top: 100px;
}

.me {
  max-width: 600px;
  margin: 0 auto;
}
.icon-container {
  background-color: var(--onBackground);
  width: 96px;
  height: 96px;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}
.section {
  position: relative;
  width: 100%;
  margin: 40px 0;
  color: var(--textColor);
  font-size: 1.2rem;
  font-weight: 700;
}
.section::after {
  content: '';
  position: absolute;
  height: 1px;
  right: 0;
  left: 0;
  top: -20px;
  background-color: var(--borderColor);
}

.personnal-info {
  display: flex;
  flex-direction: column;
}
.names {
  display: flex;
}
.row {
  flex: 1;
  margin: 15px 0;
}
.label {
  font-weight: 600;
  color: var(--secondaryTextColor);
}
</style>
