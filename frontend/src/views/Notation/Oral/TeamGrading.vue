<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue';
import TeamGradingAsOS from './components/TeamGradingAsOS.vue';
import TeamGradingAsProf from './components/TeamGradingAsProf.vue';
import { toast } from 'vue3-toastify';
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/useUserStore';
import type { Team } from '@/services/types';
import { teamService } from '@/services/team.service';
import Loading from '@/components/Loading.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';
import Comments from '@/views/CommentsFeedbacks/components/Comments.vue';
import Feedbacks from '@/views/CommentsFeedbacks/components/Feedbacks.vue';
import Empty from '@/components/Empty.vue';
import { PeopleOutline } from '@vicons/ionicons5';

const loading = ref(false);
const selectedTabMenu = ref(0);
const menuOpen = ref(true);
const teamData = ref<Team[]>([]);
const userRole = computed(() => useUserStore().getUser()?.roles);
const userId = ref(useUserStore().getUser()?.id);
const selectedSprintId = computed(() => {
  const sprintId = useUserStore().getSelectedSprintId();
  console.log('Computed selectedSprintId:', sprintId);
  return sprintId;
});
const gradeScaleReady = ref(true);

const handleGradeScaleReadyChanged = (newValue: boolean) => {
  gradeScaleReady.value = newValue;
};

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
  <Empty
    v-else-if="teamData.length === 0"
    title="Aucune équipe"
    subtitle="Pas d'équipe à afficher."
    :icon="PeopleOutline"
    bouton-string="Créer des équipes"
    path="/preparation" />
  <div v-else>
    <TeamGradingAsOS v-if="userRole?.includes('OS')" />
    <div v-else>
      <Sidebar
        :selected-tab-index="selectedTabMenu"
        :tabs="teamData.map((team) => team.name)"
        :on-click="
          (index: number) => {
            selectedTabMenu = index;
          }
        "
        :title="'Equipes (' + teamData.length + ')'"
        @menu-toggle="menuOpen = $event" />
      <n-layout
        :class="{
          'content-shifted': menuOpen,
          'content-not-shifted': !menuOpen,
        }">
        <Breadcrumb />
        <n-layout-content>
          <TeamGradingAsProf
            v-if="teamData.length > 0 && selectedTabMenu < teamData.length"
            :teamId="teamData[selectedTabMenu].id"
            :sprintId="selectedSprintId ?? 0"
            @grade-scale-ready-changed="handleGradeScaleReadyChanged" />
          <Comments
            v-if="
              gradeScaleReady && selectedSprintId && teamData[selectedTabMenu]
            "
            :id_team="teamData[selectedTabMenu]?.id"
            name="Commentaire notation oral equipe"
            :id_sprint="selectedSprintId"
            :name_team="teamData[selectedTabMenu]?.name"
            placeholder="Ecrivez votre commentaire ici."
            :id_emitter="userId" />
          <Feedbacks
            v-if="
              gradeScaleReady && selectedSprintId && teamData[selectedTabMenu]
            "
            :id_team="teamData[selectedTabMenu]?.id"
            name="Feedback notation oral equipe"
            :id_sprint="selectedSprintId"
            :name_team="teamData[selectedTabMenu]?.name"
            :id_emitter="userId" />
        </n-layout-content>
      </n-layout>
    </div>
  </div>
</template>
