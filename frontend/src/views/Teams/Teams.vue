<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue';
import { ref, computed, onMounted } from 'vue';
import type { Team } from '@/services/types';
import { useRouter } from 'vue-router';
import { teamService } from '@/services/team.service';
import { toast } from 'vue3-toastify';
import Loading from '@/components/Loading.vue';
import { useUserStore } from '@/stores/useUserStore';
import CommFeedMessageIcon from '@/views/CommentsFeedbacks/components/CommFeedMessageIcon.vue';
import NewNotesMainTable from '@/views/Teams/component/newNotesMainTable.vue';
import Empty from '@/components/Empty.vue';
import { PeopleOutline } from '@vicons/ionicons5';

const navigate = useRouter().push;

// Interface de l'équipe

// Définition des variables
const selectedTab = ref(0);
const userStore = useUserStore(); // Utilisation du store

// Récupération des données de l'équipe
const tableLoading = ref(true);
const teamData = ref<Team[] | null>(null);
const isCollapsed = ref(true);
const pageReady = ref(false);
const myTeam = ref<Team | undefined>(undefined);

onMounted(async () => {
  try {
    const teams = await teamService.getAll();
    if (teams) {
      teamData.value = teams;
      myTeam.value =
        teams.find((team) => team.supervisor.id === userStore.getUser()?.id) ||
        teams.find((team) =>
          team.users.find((u) => u.id === userStore.getUser()?.id)
        );
    }
  } catch (error) {
    toast.error('Erreur lors de la récupération des équipes.');
  }
  pageReady.value = true;
});

const selectedSprintId = computed(() => {
  return userStore.getSelectedSprintId();
});

const allTeamsNone = computed(() => {
  if (!teamData.value) return false;
  return teamData.value.every((team) => team.status === 'none');
});

const commentOpen = ref(false);
const menuOpen = ref(true);
</script>

<template>
  <Loading v-if="!pageReady" />
  <Empty
    v-else-if="!teamData"
    title="Aucune équipe"
    subtitle="Il n'y a aucune équipe à afficher"
    :icon="PeopleOutline"
    path="/"
    bouton-string="Accueil" />

  <div v-else class="teamsView">
    <h2 class="noTeam" v-if="!tableLoading && allTeamsNone">
      Les équipes n'ont pas encore été publiées.
    </h2>
    <div v-else>
      <!-- Affichage des onglets avec le nom des équipes -->
      <n-layout has-sider>
        <Sidebar
          :selected-tab-index="selectedTab"
          :tabs="teamData.map((team) => team.name)"
          :on-click="
            (index: number) => {
              selectedTab = index;
            }
          "
          :title="'Equipes (' + teamData.length + ')'"
          @menu-toggle="menuOpen = $event"
          :my-team-name="myTeam?.name" />
        <n-layout
          :class="{
            'content-shifted': menuOpen,
            'content-not-shifted': !menuOpen,
          }">
          <n-layout
            :class="{
              'comment-shifted': commentOpen,
              'comment-not-shifted': !commentOpen && !isCollapsed,
            }">
            <!-- Affichage de l'icône de réclamation-->
            <n-layout has-sider sider-placement="right">
              <n-layout-content>
                <Loading v-show="tableLoading" />
                <div v-show="!tableLoading">
                  <n-card :bordered="false" class="primary-container">
                    <div v-if="isCollapsed" class="comment-icon-container">
                      <p class="title-comment-button">
                        Retours
                        {{
                          userStore.getUser()?.roles?.includes('OS')
                            ? ''
                            : '& Commentaires'
                        }}
                      </p>
                      <n-button
                        @click="
                          isCollapsed = false;
                          commentOpen = true;
                        "
                        class="comments-button">
                        <i class="fa-solid fa-comments" />
                      </n-button>
                    </div>
                    <div class="content">
                      <!-- Si les teams n'existent pas -->
                      <div v-if="!teamData.length" class="noTeam">
                        <p class="msg">
                          Les équipes n'ont pas encore été créées.
                        </p>
                        <n-button @click="navigate('/preparation')">
                          Créer les équipes
                        </n-button>
                      </div>
                      <div v-else>
                        <NewNotesMainTable
                          :selectedSprintId="selectedSprintId || 0"
                          :selected-tab="selectedTab"
                          :set-loading="
                            (state: boolean) => (tableLoading = state)
                          "
                          @update:selectedTab="
                            (newTab: any) => (selectedTab = newTab)
                          " />
                      </div>
                    </div>
                  </n-card>
                </div>
              </n-layout-content>
              <n-layout-sider
                class="layout-sider"
                collapse-mode="width"
                :collapsed-width="50"
                :native-scrollbar="true"
                :width="340"
                v-model:collapsed="isCollapsed"
                icon-size="70px"
                content-style="padding: 7px;"
                bordered
                @collapse="commentOpen = false"
                @expand="commentOpen = true"
                :style="{ display: isCollapsed ? 'none' : 'block' }">
                <div class="header-comment-container">
                  <div
                    @click="
                      isCollapsed = true;
                      commentOpen = false;
                    "
                    class="cross-button">
                    <i class="fa-solid fa-square-xmark" />
                  </div>
                  <div class="main-icon-comment">
                    <i class="fa-regular fa-comments" />
                  </div>
                </div>
                <CommFeedMessageIcon
                  class="padding-comments"
                  v-show="!isCollapsed"
                  :id_team="teamData[selectedTab]?.id"
                  :id_sprint="selectedSprintId || 0" />
              </n-layout-sider>
            </n-layout>
          </n-layout>
        </n-layout>
      </n-layout>
    </div>
  </div>
</template>

<style scoped>
.content {
  margin-top: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.layout-sider {
  position: fixed;
  right: 0;
  height: 100vh;
  background-color: var(--backgroundColor);
  border: 1px solid var(--borderColor);
}
.padding-comments {
  padding: 10px;
}
.noTeam {
  text-align: center;
  margin-top: 300px;
}
.comment-icon-container {
  display: flex;
  justify-content: right;
  font-size: 40px;
  margin-top: 10px;
  margin-right: 50px;
}
.header-comment-container {
  display: flex;
  justify-content: space-between;
  font-size: 20px;
  margin-top: 5px;
  margin-left: 5px;
}
.title-comment-button {
  display: flex;
  align-items: center;
  font-size: 14px;
  text-align: center;
  margin: 10px;
}
.comments-button {
  background-color: var(--backgroundColor);
  color: var(--primaryColor);
  border: 1px solid var(--borderColor);
  border-radius: 15%;
  width: 50px;
  height: 50px;
  font-size: 20px;
}
.cross-button {
  background: none;
  border-color: var(--borderColor);
  border-width: 0;
  padding: 0;
  color: #000;
  font-size: 25px;
}
.main-icon-comment {
  display: flex;
  flex: 12;
  justify-content: center;
  font-size: 40px;
  margin-top: 15px;
  margin-bottom: 15px;
}
.comment-shifted {
  margin-right: 340px;
  transition: margin-right 0.5s ease-out;
}
.comment-not-shifted {
  margin-right: 50px;
  transition: margin-right 0.5s ease-out;
}
</style>
