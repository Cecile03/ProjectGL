<script lang="ts">
import { computed, defineComponent, h, onMounted, ref, watch } from 'vue';
import { NInputNumber } from 'naive-ui';
import { toast } from 'vue3-toastify';
import { useUserStore } from '@/stores/useUserStore';
import Loading from '@/components/Loading.vue';
import { teamService } from '@/services/team.service';
import { subGradeService } from '@/services/subgrade.service';
import { evaluationService } from '@/services/evaluation.service';
import Sidebar from '@/components/Sidebar.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';
import GradeScaleInfoIcon from '@/components/GradeScaleInfoIcon.vue';
import Empty from '@/components/Empty.vue';
import { PeopleOutline } from '@vicons/ionicons5';

export default defineComponent({
  name: 'NotationProf',
  components: { GradeScaleInfoIcon, Loading, Sidebar, Breadcrumb },
  setup() {
    type Team = {
      label: string;
      value: number;
      supervisor: User;
    };

    type User = {
      firstName: string;
      lastName: string;
    };

    type DataType = {
      id: number;
      name: string;
      note: number;
      evaluationId: number;
    }[];

    const teamOptions = ref<Team[]>([]);
    const user = useUserStore().getUser();
    const selectedTeam = ref(0);
    const selectedSprintId = computed(() =>
      useUserStore().getSelectedSprintId()
    );
    const menuOpen = ref(true);

    const fetchTeams = async () => {
      try {
        const teams = await teamService.getAll();
        if (teams) {
          teamOptions.value = teams.map((team: any) => {
            const teamData: Team = {
              label: team.name,
              value: team.id,
              supervisor: team.supervisor,
            };
            return teamData;
          });
        }
      } catch (error) {
        console.error('Erreur lors de la récupération des équipes:', error);
      }
    };

    const data = ref<DataType>([]);

    //Requête pour parser les infos récupérées de l'api
    //Requête pour parser les infos récupérées de l'api
    const fetchUsers = async () => {
      try {
        const response = await teamService.getTeamById(
          teamOptions.value[selectedTeam.value].value
        );
        const users = response.users;
        console.log(response.users);
        // Sort users by last name, then first name
        users.sort((a: any, b: any) => {
          const nameA = `${a.lastName} ${a.firstName}`.toUpperCase();
          const nameB = `${b.lastName} ${b.firstName}`.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
        data.value = users.map((user: any) => {
          const userData: {
            id: number;
            name: string;
            note: number;
            evaluationId: number;
          } = {
            id: user.id,
            name: `${user.firstName} ${user.lastName}`,
            note: user.note || 0,
            evaluationId: user.evaluationId || 0,
          };
          return userData;
        });
        await resetNotes();
      } catch (error) {
        console.error('Erreur:', error);
      }
    };

    // Surveillez les changements de selectedTeam et déclenchez fetchUsers chaque fois que selectedTeam change
    watch(
      () => selectedTeam.value,
      async () => {
        console.log('fetchUsers called');
        if (selectedTeam.value !== null) {
          await fetchUsers();
          console.log(data.value);
        }
      },
      { immediate: true }
    );

    const pageNotReady = ref(true);

    onMounted(async () => {
      await fetchTeams();
      await fetchUsers();
      pageNotReady.value = false;
    });

    const isEditing = ref(false);

    const columns = ref([
      {
        title: 'Nom Prénom',
        key: 'name',
        render(row: { name: string }) {
          return row.name;
        },
      },
      {
        title: 'Note Présentation Individiuelle',
        key: 'note',
        render(row: { note: number | null }) {
          if (isEditing.value) {
            return h(NInputNumber, {
              value: row.note,
              min: 0,
              max: 20,
              onUpdateValue: (value) => (row.note = value),
            });
          } else {
            return `${row.note} / 20`;
          }
        },
      },
    ]);

    const resetNotes = async () => {
      if (selectedSprintId.value === null || user?.id === undefined) {
        return;
      }
      for (const student of data.value) {
        try {
          const userGrade = await subGradeService.getEvaluationBySubgrade(
            student.id,
            selectedSprintId.value,
            user.roles.includes('SS') ? 'SSPR' : 'TCPR',
            user.id
          );
          student.note = userGrade.value ? userGrade.value : 0;
          student.evaluationId = userGrade.id;
        } catch (error) {
          student.note = 0;
          console.log(error);
        }
      }
    };

    const resetNotesWithNotif = () => {
      resetNotes();
      notifyReseting();
      isEditing.value = false;
    };

    const validateNotes = () => {
      try {
        data.value.forEach((student) => {
          console.log('student', student);
          evaluationService.updateEvaluation(
            student.evaluationId,
            student.note
          );
        });
        notifyValidating();
        isEditing.value = false;
      } catch (error) {
        console.error('Erreur:', error);
      }
    };

    const notifyEditing = () => {
      toast.info('Vous entrez dans le mode modification', {
        autoClose: 500,
      });
    };

    const notifyReseting = () => {
      toast.success('Toutes les notes ont été réinitialisées', {
        autoClose: 500,
      });
    };

    const notifyValidating = () => {
      toast.success('Les changements ont bien été pris en compte', {
        autoClose: 500,
      });
    };

    const getTeamName = () => {
      const team = teamOptions.value[selectedTeam.value];
      return team ? team.label : '';
    };

    const getSupervisorName = () => {
      const team = teamOptions.value[selectedTeam.value];
      return team
        ? `${team.supervisor.firstName} ${team.supervisor.lastName}`
        : '';
    };

    return {
      columns,
      data,
      isEditing,
      notifyEditing,
      teamOptions,
      selectedTeam,
      getTeamName,
      getSupervisorName,
      resetNotesWithNotif,
      validateNotes,
      pageNotReady,
      menuOpen,
      PeopleOutline,
    };
  },
});
</script>

<template>
  <Loading v-if="pageNotReady" />
  <Empty
    v-else-if="teamOptions.length === 0"
    title="Aucune équipe"
    subtitle="Pas d'équipe à afficher."
    :icon="PeopleOutline"
    bouton-string="Créer des équipes"
    path="/preparation" />
  <div v-else class="container">
    <Sidebar
      :selected-tab-index="selectedTeam"
      :tabs="teamOptions.map((team) => team.label)"
      :on-click="
        (index: number) => {
          selectedTeam = index;
        }
      "
      :title="'Equipes (' + teamOptions.length + ')'"
      @menu-toggle="menuOpen = $event" />
    <n-layout
      :class="{
        'content-shifted': menuOpen,
        'content-not-shifted': !menuOpen,
      }">
      <Breadcrumb />
      <n-layout-content class="wrapper">
        <div class="header">
          <GradeScaleInfoIcon />
          <!-- Afficher le nom de l'équipe sélectionnée -->
          <p class="text-color">Equipe : {{ getTeamName() }}</p>
          <div class="header-icons">
            <n-tooltip trigger="hover">
              <template #trigger>
                <i
                  :class="
                    isEditing
                      ? 'fa-solid fa-pen-to-square'
                      : 'fa-regular fa-pen-to-square'
                  "
                  @click="
                    () => {
                      isEditing = !isEditing;
                    }
                  " />
              </template>
              Modifier
            </n-tooltip>
            <!-- Afficher le nom du superviseur de l'équipe sélectionnée -->
            <p class="text-color">
              Professeur encadrant : {{ getSupervisorName() }}
            </p>
            <i
              :class="isEditing ? 'fa-regular fa-circle-xmark' : ''"
              @click="resetNotesWithNotif"></i>
            <i
              :class="isEditing ? 'fa-regular fa-circle-check' : ''"
              @click="validateNotes"></i>
          </div>
        </div>
        <n-card class="secondary-container">
          <n-data-table :columns="columns" :data="data" />
        </n-card>
      </n-layout-content>
    </n-layout>
  </div>
</template>

<style scoped>
.secondary-container {
  border: none;
}
.container {
  display: flex;
}
.wrapper {
  padding: 20px 50px 50px 50px;
}
.header {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--textColor);
}
.header-icons {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-icons .fa-pen-to-square {
  /* Icone modifier */
  font-size: 28px;
  padding-left: 20px;
}
.text-color {
  flex-grow: 1;
  text-align: center;
}
.header-icons .fa-circle-xmark,
.header-icons .fa-circle-check {
  font-size: 28px;
  padding-right: 20px;
}

@media (max-width: 768px) {
  .content {
    width: calc(100%);
  }
  .header-icons {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .header-icons .fa-pen-to-square {
    /* Icone modifier */
    font-size: 20px;
    padding-left: 20px;
  }
  .text-color {
    flex-grow: 1;
    text-align: center;
  }
  .header-icons .fa-circle-xmark,
  .header-icons .fa-circle-check {
    font-size: 20px;
    padding-right: 20px;
  }
}
</style>
