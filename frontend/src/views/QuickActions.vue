<script setup lang="ts">
import { useUserStore } from '@/stores/useUserStore';
import {
  People,
  Person,
  Book,
  Flag,
  Notifications,
  SettingsSharp,
  FolderOpen,
} from '@vicons/ionicons5';
import { AddSubtractCircle24Filled as BMIcon } from '@vicons/fluent';
import HubMenu from '@/components/HubMenu.vue';
import type { Navigator } from '@/components/Navigator.vue';

const user = useUserStore().getUser();
console.log('user role', user?.roles);

const navigatorsOS: Navigator[] = [
  {
    title: 'Profil',
    description: 'Afficher ses informations personnelles',
    icon: Person,
    redirectTo: '/profile',
  },
  {
    title: 'Equipes',
    description: 'Afficher les équipes, les notes et les retours',
    icon: People,
    redirectTo: '/teams',
  },
  {
    title: 'Notation',
    description: "Définir l'ordre de passage, les bonus/malus et noter l'oral",
    icon: Book,
    redirectTo: '/notation',
  },
  {
    title: 'Signalement',
    description:
      'Effectuer ou voir les signalements des équipes et les valider/refuser',
    icon: Flag,
    redirectTo: '/flag',
  },
  {
    title: 'Notifications',
    description: 'Voir les notifications reçues',
    icon: Notifications,
    redirectTo: '/notification',
  },
];

const navigatorsPL: Navigator[] = [
  {
    title: 'Preparation',
    description: 'Définir les paramètres du projet',
    icon: SettingsSharp,
    redirectTo: '/preparation',
  },
  {
    title: 'Equipes',
    description:
      'Afficher les équipes, les notes, les commentaires et les retours',
    icon: People,
    redirectTo: '/teams',
  },
  {
    title: 'Notation',
    description:
      "Noter la performance orale individuelle/équipe et visualiser l'ordre de passage",
    icon: Book,
    redirectTo: '/notation',
  },
  {
    title: 'Signalement',
    description: 'Voir les signalements des équipes et les valider/refuser',
    icon: Flag,
    redirectTo: '/flag',
  },
  {
    title: 'Notifications',
    description: 'Voir les notifications reçues',
    icon: Notifications,
    redirectTo: '/notification',
  },
];

const navigatorsStaff: Navigator[] = [
  {
    title: 'Equipes',
    description:
      'Afficher les équipes, les notes, les commentaires et les retours',
    icon: People,
    redirectTo: '/teams',
  },
  {
    title: 'Notation',
    description:
      "Noter l'oral individuel/équipe et visualiser l'ordre de passage de toutes les équipes",
    icon: Book,
    redirectTo: '/notation',
  },
  {
    title: 'Projet',
    description: 'Noter le projet pour votre équipe encadrée',
    icon: FolderOpen,
    redirectTo: '/notation/project',
  },
  {
    title: 'Bonus/Malus',
    description: 'Attribuer des bonus/malus à votre équipe encadrée',
    icon: BMIcon,
    redirectTo: '/notation/bm',
  },
  {
    title: 'Signalement',
    description: 'Voir les signalements des équipes et les valider/refuser',
    icon: Flag,
    redirectTo: '/flag',
  },
];
const navigatorsTC: Navigator[] = [
  {
    title: 'Equipes',
    description:
      'Afficher les équipes, les notes, les commentaires et les retours',
    icon: People,
    redirectTo: '/teams',
  },
  {
    title: 'Notation',
    description:
      "Noter l'oral individuel/équipe et visualiser l'ordre de passage de toutes les équipes",
    icon: Book,
    redirectTo: '/notation',
  },
  {
    title: 'Projet',
    description: 'Noter le projet pour votre équipe encadrée',
    icon: FolderOpen,
    redirectTo: '/notation/project',
  },
];
</script>

<template>
  <div>
    <HubMenu :navigators="navigatorsOS" v-if="user?.roles.includes('OS')" />
    <HubMenu
      :navigators="navigatorsPL"
      v-else-if="user?.roles.includes('PL')" />
    <HubMenu
      :navigators="navigatorsStaff"
      v-else-if="user?.roles.includes('SS')" />
    <HubMenu
      :navigators="navigatorsTC"
      v-else-if="user?.roles.includes('TC')" />
  </div>
</template>

<style scoped></style>
