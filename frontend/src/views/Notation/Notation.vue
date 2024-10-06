<script setup lang="ts">
import { useUserStore } from '@/stores/useUserStore';
import { formatUserRoles } from '@/utils/utils';
import { FolderOpen, ListCircle } from '@vicons/ionicons5';
import {
  AddSubtractCircle24Filled as BMIcon,
  ProjectionScreen24Filled as TeamOral,
  ShareScreenPerson24Filled as IndivOral,
} from '@vicons/fluent';
import HubMenu from '@/components/HubMenu.vue';
import GradingImg from '@/assets/img/grading.png';

const userRole = useUserStore().getUser()?.roles;

const navigators = userRole?.includes('OS')
  ? [
      {
        title: 'Ordre passage',
        description:
          'Déclarez votre ordre de passage durant votre sprint review',
        icon: ListCircle,
        redirectTo: '/notation/oral/runningOrder',
      },
      {
        title: 'Oral - équipes',
        description: 'Notez la Sprint Review oral de chaque équipe',
        icon: TeamOral,
        redirectTo: '/notation/oral/teams',
      },
      {
        title: 'Bonus et Malus',
        description:
          'Attribuez des bonus et malus limités au sein de votre équipe',
        icon: BMIcon,
        redirectTo: '/notation/bm',
      },
    ]
  : [
      {
        title: 'Ordre de passage',
        description:
          'Visualisez les ordres de passage de chaque équipe pour la Sprint Review',
        icon: ListCircle,
        redirectTo: '/notation/oral/runningOrder',
      },
      {
        title: 'Oral - équipes',
        description: 'Notez la Sprint Review oral de chaque équipe',
        icon: TeamOral,
        redirectTo: '/notation/oral/teams',
      },
      {
        title: 'Oral - individuel',
        description:
          "Notez la Sprint Review oral de chaque membre d'une équipe",
        icon: IndivOral,
        redirectTo: '/notation/oral/indiv',
      },
    ];
if (userRole?.includes('SS') || userRole?.includes('TC')) {
  navigators.push({
    title: 'Projet',
    description: 'Notez le projet pour votre équipe encadrée',
    icon: FolderOpen,
    redirectTo: '/notation/project',
  });
}
if (userRole?.includes('SS') && !userRole?.includes('OS')) {
  navigators.push({
    title: 'Bonus et Malus',
    description:
      'Attribuez des bonus et malus ilimités pour votre équipe encadrée',
    icon: BMIcon,
    redirectTo: '/notation/bm',
  });
}
</script>
<template>
  <router-view v-slot="{ Component, route }">
    <component :is="Component" v-if="route.name !== 'Notation'" />
    <div class="notation" v-else>
      <h1 class="title">Notation</h1>
      <p class="role">
        En tant que <span class="value">{{ formatUserRoles(userRole) }}</span
        >, vous avez la possibilité de noter les éléments suivants.
      </p>
      <img class="img-grade" :src="GradingImg" preload />
      <HubMenu :navigators="navigators" />
    </div>
  </router-view>
</template>

<style scoped>
.notation {
  padding: 0 150px;
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
}
.role .value {
  font-weight: 400;
  color: var(--primaryColor);
}
.img-grade {
  width: 100px;
  display: block;
  margin: 20px auto 50px auto;
}

@media screen and (max-width: 900px) {
  .notation {
    padding: 0 50px;
  }
}
</style>
