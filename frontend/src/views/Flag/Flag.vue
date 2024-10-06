<script setup lang="ts">
import { useUserStore } from '@/stores/useUserStore';
import { formatUserRoles } from '@/utils/utils';
import {
  Flag,
  ListCircle,
  PersonOutline,
  PeopleOutline,
} from '@vicons/ionicons5';
import { DragDrop } from '@vicons/tabler';
import { Vote24Regular, Info24Regular } from '@vicons/fluent';
import { CheckCircleOutlined, BlockOutlined } from '@vicons/material';
import Navigator from '@/components/Navigator.vue';

const userRole = useUserStore().getUser()?.roles;

const navigators =
  userRole?.includes('OS') || userRole?.includes('SS')
    ? [
        {
          title: 'Effectuer un signalement',
          description: 'Effectuer les changements désirés pour votre équipe',
          icon: Flag,
          redirectTo: '/flag/flagInstanciate',
          explaination: [
            {
              icon: DragDrop,
              text: 'Glisser déposer les étudiants à changer d’équipe ',
            },
            {
              icon: PersonOutline,
              text: 'Le signalement doit concerner votre équipe',
            },
            {
              icon: PeopleOutline,
              text: 'Le signalement peut concerner que 2 équipes',
            },
          ],
        },
        {
          title: 'Voir les signalements',
          description: 'Afficher les signalements effectués par les équipes',
          icon: ListCircle,
          redirectTo: userRole?.includes('OS')
            ? '/flag/flagView'
            : '/flag/flagViewPL',
          explaination: [
            {
              icon: Vote24Regular,
              text: 'Voter pour le signalement : vous devez voter pour ou contre le signalement pour qu’il soit validé',
            },
            {
              icon: CheckCircleOutlined,
              text: userRole?.includes('OS')
                ? 'Si tous les étudiants concernés et 1 professeur valident le signalement, les changements prennent effet.'
                : 'Une fois que tous les étudiants ont voté pour, vous pouvez valider pour que les changements prennent effet.',
            },
            {
              icon: BlockOutlined,
              text: 'Refus : Le signalement est supprimé',
            },
          ],
        },
      ]
    : [
        {
          title: 'Voir les signalements',
          description: 'Afficher les signalements effectués par les équipes',
          icon: ListCircle,
          redirectTo: userRole?.includes('OS')
            ? '/flag/flagView'
            : '/flag/flagViewPL',
          explaination: [
            {
              icon: Vote24Regular,
              text: 'Voter pour le signalement : vous devez voter pour ou contre le signalement pour qu’il soit validé',
            },
            {
              icon: CheckCircleOutlined,
              text: 'Une fois que tous les étudiants ont voté pour, vous pouvez valider pour que les changements prennent effet.',
            },
            {
              icon: BlockOutlined,
              text: 'Refus : Le signalement est supprimé',
            },
          ],
        },
      ];
</script>

<template>
  <router-view v-slot="{ Component, route }">
    <component :is="Component" v-if="route.name !== 'Signalement'" />
    <div class="notation" v-else>
      <h1 class="title">Signalement</h1>
      <p class="role" v-if="userRole?.includes('PL')">
        En tant que <span class="value">{{ formatUserRoles(userRole) }}</span
        >, vous avez la possibilité d'afficher et valider des signalements.
      </p>
      <p class="role" v-else>
        En tant que <span class="value">{{ formatUserRoles(userRole) }}</span
        >, vous avez la possibilité d'effectuer, afficher et valider des
        signalements.
      </p>
      <div class="info">
        <n-icon class="icon" :size="18" :component="Info24Regular"></n-icon>
        <p>
          Cliquez sur l'une des cartes suivantes pour vous rendre à la page
          correspondante.
        </p>
      </div>
      <div class="navigator-parent">
        <div
          v-for="navigator in navigators"
          :key="navigator.title"
          class="navigator-container">
          <Navigator :navigator="navigator" class="navigator-item" />
          <div class="explaination">
            <div
              class="icon-text"
              v-for="exp in navigator.explaination"
              :key="exp.text">
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)' }">
                <n-icon
                  :component="exp.icon"
                  size="24"
                  color="var(--textColor)" />
              </n-tag>
              <span color="var(--textColor)">{{ exp.text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </router-view>
</template>

<style scoped>
.notation {
  padding: 0 200px;
}
.title {
  text-align: center;
  margin-top: 100px;
  font-size: 1.5rem;
  color: var(--textColor);
}

.role {
  max-width: 800px;
  width: fit-content;
  margin: 10px auto;
  margin-bottom: 40px;
}
.role .value {
  font-weight: 400;
  color: var(--primaryColor);
}

.navigator-container {
  display: flex;
}

.navigator-item {
  margin: 0px;
  justify-content: center;
}

.explaination {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  margin-left: 40px;
  padding-left: 40px;
  position: relative;
}

.explaination::before {
  content: '';
  position: absolute;
  top: 20px; /* Marge supérieure */
  bottom: 20px; /* Marge inférieure */
  left: 0;
  border-left: 1px solid #8c8c8c;
}

.icon-text {
  display: flex;
  align-items: center;
  padding: 5px;
  gap: 5px;
}

.navigator-parent {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.info {
  display: flex;
  align-items: center;
  margin-top: 50px;
  margin-bottom: 10px;
}

@media screen and (max-width: 900px) {
  .notation {
    padding: 0 50px;
  }
}
</style>
