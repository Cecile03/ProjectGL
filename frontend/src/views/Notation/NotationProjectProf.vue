<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import Sidebar from '@/components/Sidebar.vue';
import NotationEquipe from '@/views/Notation/components/NotationEquipe.vue';
import { teamService } from '@/services/team.service';
import Comments from '@/views/CommentsFeedbacks/components/Comments.vue';
import { useUserStore } from '@/stores/useUserStore';
import Loading from '@/components/Loading.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';
import { PeopleOutline } from '@vicons/ionicons5';

const nbTeam = ref<number>(0);
let TABS: string[] = [];
let teamNames: string[] = [];
let teamId: number[] = [];
const selectedTab = ref(0);
const loading = ref(false);

const userStore = useUserStore();
const userRole = userStore.getUser()?.roles;
const userId = ref(userStore.getUser()?.id);
const sprintId = computed(() => useUserStore().getSelectedSprintId());

onMounted(async () => {
  loading.value = true;
  try {
    const response = await teamService.getAll();
    if (response) {
      if (userRole?.includes('SS')) {
        const ssTeam = response.find(
          (team) => team.supervisor.id == userId.value
        );
        nbTeam.value = 1;
        if (ssTeam) {
          TABS = [ssTeam.name];
          teamNames = [ssTeam.name];
          teamId = [ssTeam.id];
        }
        // not sure about this
      } else if (userRole?.includes('TC')) {
        nbTeam.value = response.length;
        TABS = response.map((team, index) => `${team.name}`);
        teamNames = response.map((team) => team.name);
        teamId = response.map((team) => team.id);
      }
      console.log('nbTeam', nbTeam.value);
    }
  } catch (error) {
    console.error(error);
  }
  loading.value = false;
});

watch(sprintId, async () => {
  console.log('watch sprintId', sprintId.value);
});
const menuOpen = ref(true);
</script>

<template>
  <Loading v-if="loading" />
  <div v-else-if="nbTeam == 0">
    <Breadcrumb />
    <Empty
      title="Aucune équipe"
      subtitle="Il n'y a aucune équipe à afficher"
      :icon="PeopleOutline"
      path="/notation"
      bouton-string="Retour" />
  </div>
  <div v-else class="notationProfView">
    <Sidebar
      :selected-tab-index="selectedTab"
      :tabs="TABS"
      :on-click="
        (index) => {
          selectedTab = index;
        }
      "
      :title="'Equipes (' + TABS.length + ')'"
      @menu-toggle="menuOpen = $event" />
    <n-layout
      :class="{
        'content-shifted': menuOpen,
        'content-not-shifted': !menuOpen,
      }">
      <Breadcrumb />
      <div class="equipe">
        <NotationEquipe
          :teamId="teamId[selectedTab]"
          :teamName="teamNames[selectedTab]"
          :sprintId="sprintId || 0" />

        <Comments
          v-if="userRole?.includes('TC') && sprintId"
          class="comment-section"
          name="Solutions techniques"
          :id_team="teamId[selectedTab]"
          :id_emitter="userId"
          :id_sprint="sprintId"
          :name_team="teamNames[selectedTab]"
          placeholder="Ecrivez votre commentaire sur la solution technique et la gestion de projet." />
      </div>
    </n-layout>
  </div>
</template>

<style>
.notationProfView {
  display: flex;
  flex-direction: column;
}

.noTeam {
  text-align: center;
  margin-top: 300px;
}

.comment-section {
  margin: 40px;
}
</style>
