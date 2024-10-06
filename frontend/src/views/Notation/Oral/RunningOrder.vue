<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue';
import RunningOrderAsOS from './components/RunningOrderAsOS.vue';
import RunningOrderAsProf from './components/RunningOrderAsProf.vue';
import { toast } from 'vue3-toastify';
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/useUserStore';
import type { Team } from '@/services/types';
import { teamService } from '@/services/team.service';
import Loading from '@/components/Loading.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';

const loading = ref(false);
const selectedTabMenu = ref(0);
const menuOpen = ref(true);
const teamData = ref<Team[]>([]);
const userRole = computed(() => useUserStore().getUser()?.roles);
const selectedSprintId = computed(() => {
  const sprintId = useUserStore().getSelectedSprintId();
  console.log('Computed selectedSprintId:', sprintId);
  return sprintId;
});

onMounted(async () => {
  try {
    loading.value = true;
    const teams = await teamService.getAll();
    console.log('teams', teams);
    if (teams) {
      teamData.value = teams;
    }
  } catch (error) {
    toast.error('Erreur lors de la récupération des équipes.');
  }
  loading.value = false;
});
</script>

<template>
  <div v-if="loading">
    <Loading />
  </div>
  <div v-else>
    <div v-if="userRole?.includes('OS')">
      <Breadcrumb />
      <RunningOrderAsOS />
    </div>
    <div v-else>
      <Sidebar
        :selected-tab-index="selectedTabMenu"
        :tabs="teamData.map((team) => team.name)"
        :on-click="(index: number) => {
          selectedTabMenu = index;
        }"
        :title="'Equipes (' + teamData.length + ')'"
        @menu-toggle="menuOpen = $event"
      />
      <n-layout :class="{
        'content-shifted': menuOpen,
        'content-not-shifted': !menuOpen,
      }">
        <Breadcrumb />
        <n-layout-content>
            <RunningOrderAsProf
              v-if="teamData.length > 0 && selectedTabMenu < teamData.length"
              :teamId="teamData[selectedTabMenu].id"
              :sprintId="selectedSprintId ?? 0"
            />
        </n-layout-content>
      </n-layout>
    </div>
  </div>
</template>
