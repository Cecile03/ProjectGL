<script setup lang="ts">
import { ref, h, withDefaults, defineProps, defineEmits, computed, watch } from 'vue';
import type { DataTableColumns, DataTableCreateSummary } from 'naive-ui';
import { useUserStore } from '@/stores/useUserStore';
import Info16Regular from '@vicons/fluent/Info16Regular';
import type { Team } from '@/services/types';
import { teamService } from '@/services/team.service';

//Import du store
const userStore = useUserStore();
const user = userStore.getUser();

const isDataLoaded = ref(false);

//Définition du type pour les données de la table
type RowData = {
  name: string;
  gender: string;
  gradePast: number;
};

//Définition des props avec valeurs par défaut
const props = withDefaults(
  defineProps<{
    selectedTab: number;
  }>(),
  {
    selectedTab: 1,
    role: 1,
  }
);

const emit = defineEmits(['update:selectedTab']);

const teamData = ref<Team[]>([]);

//Fonction pour gérer la navigation entre les onglets
const handleClick = (newTab: number) => {
  emit('update:selectedTab', newTab);
};

const validateTeam2 = async () => {
  if (teamData.value.length > props.selectedTab) {
    const team = teamData.value[props.selectedTab];
    await teamService.validateTeamSS(team.id);
    team.validated = true;
  }
};
//Récupération des données des équipes
const fetchTeamData = async () => {
  const teams = await teamService.getAll();
  if (teams) {
    teamData.value = teams;
  }

  isDataLoaded.value = true;
  columns.value = createColumns();
};

//Création des colonnes de la table
const createColumns = (): DataTableColumns<RowData> => {
  let columns = [
    {
      title: 'Nom - Prénom',
      key: 'name',
    },
    {
      title: 'M - F',
      key: 'gender',
    },
  ];

  if (
    teamData.value.length > props.selectedTab &&
    (isUserInTeam(teamData.value[props.selectedTab]) ||
      user?.roles.includes('SS') ||
      user?.roles.includes('OL') ||
      user?.roles.includes('PL'))
  ) {
    columns.push({
      title: 'Moyenne',
      key: 'gradePast',
    });
  }

  return columns;
};

const columns = ref(createColumns());
console.log(columns.value);
//Affichage de la moyenne générale de l'équipe quand l'utilisateur est professeur
const createSummary: DataTableCreateSummary = (pageData) => {
  if (user?.roles.includes('OS')) {
    return [];
  }
  console.log('createSummary called with data:', pageData);
  const total = (pageData as RowData[]).reduce(
    (prevValue, row) =>
      prevValue + row.gradePast,
    0
  );
  return {
    name: {
      value: h(
        'span',
        { style: { fontWeight: 'bold', color: 'black' } },
        "Moyenne générale de l'équipe : " + (total / pageData.length).toFixed(2)
      ),
      colSpan: 3,
    },
  };
};

const label = computed(() => {
  if (teamData.value.length > props.selectedTab) {
    const criteria = teamData.value[props.selectedTab].criteria;
    return [
      `Critères de création d'équipes:`,
      `Nombre de filles: ${criteria.numberOfGirls}`,
      `Nombre de bachelors: ${criteria.numberOfBachelor}`,
      `Ecart de moyennes: ${criteria.minAverageThreshold.toFixed(2)}`,
    ];
  }
  return [];
});

const isUserInTeam = (team: Team) => {
  return user && team.users.some((member) => member.id === user.id);
};

watch(
  () => props.selectedTab,
  () => {
    columns.value = createColumns();
  }
);

//Appel pour récupérer les données des équipes
fetchTeamData();
</script>

<template>
  <div class="table-container" v-if="isDataLoaded">
    <div class="table-container">
      <!-- Affichage du nom de l'équipe et du professeur référent -->
      <div class="teamInfo">
        <h2 class="teamName">
          {{
            teamData.length > props.selectedTab
              ? teamData[props.selectedTab].name
              : 'Loading...'
          }}
        </h2>
        <n-popover trigger="hover">
          <template #trigger>
            <n-icon class="info-icon" :size="18" :component="Info16Regular" />
          </template>
          <div>
            <strong>{{ label[0] }}</strong>
            <div v-for="(line, index) in label.slice(1)" :key="index">
              {{ line }}
            </div>
          </div>
        </n-popover>
      </div>
      <h4 class="profName">
        Professeur référent :
        <span
          v-if="
            teamData.length > props.selectedTab &&
            teamData[props.selectedTab].supervisor
          ">
          {{ teamData[props.selectedTab].supervisor.firstName }}
          {{ teamData[props.selectedTab].supervisor.lastName }}
        </span>
        <span v-else> Loading... </span>
      </h4>
      <div class="content">
        <span
          v-if="props.selectedTab != 0"
          @click="handleClick(Math.max(0, props.selectedTab - 1))">
          <i class="fa-solid fa-chevron-left"></i>
        </span>
        <span v-else>
          <i
            class="fa-solid fa-chevron-left disabled-arrow"
            style="color: grey"></i>
        </span>
        <!-- Affichage du tableau -->
        <n-data-table
          ref="table"
          :summary="createSummary"
          :single-line="false"
          :columns="columns"
          :data="
            teamData.length > props.selectedTab
              ? teamData[props.selectedTab].users.map((userInfo) => {
                  const userData: {
                    name: string;
                    gender: string;
                    gradePast?: number;
                  } = {
                    name: userInfo.firstName + ' ' + userInfo.lastName,
                    gender: userInfo.gender === 'male' ? 'M' : 'F',
                    gradePast: userInfo.gradePast,
                  };
                  return userData;
                })
              : []
          " />
        <span
          v-if="props.selectedTab != teamData.length - 1"
          @click="
            handleClick(Math.min(teamData.length - 1, props.selectedTab + 1))
          ">
          <i class="fa-solid fa-chevron-right"></i>
        </span>
        <span v-else>
          <i
            class="fa-solid fa-chevron-right disabled-arrow"
            style="color: grey"></i>
        </span>
      </div>
      <n-button
        v-if="
          teamData.length > props.selectedTab &&
          teamData[props.selectedTab].status === 'prepublish' &&
          teamData[props.selectedTab].supervisor.id === user?.id
        "
        type="default"
        class="validateButton"
        @click="validateTeam2"
      >
        Valider mon équipe
      </n-button>
    </div>
  </div>
  <div v-else>Loading...</div>
</template>

<style scoped>
.table-container {
  width: 70%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 5px;
}
.teamInfo {
  display: flex;
  align-items: center;
  justify-content: center;
}
.teamName {
  margin-bottom: 5px;
}
.profName {
  margin-bottom: 10px;
}
.content {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 90%;
}
.fa-chevron-left {
  font-size: 30px;
  margin-right: 20px;
  cursor: pointer;
}
.fa-chevron-right {
  font-size: 30px;
  margin-left: 20px;
  cursor: pointer;
}
.disabled-arrow {
  cursor: not-allowed;
}
.info-icon {
  margin: 10px;
  cursor: pointer;
}

.validateButton {
  margin-top: 20px;
}
</style>
