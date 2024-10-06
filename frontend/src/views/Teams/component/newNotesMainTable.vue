<script setup lang="ts">
import { computed, h, onMounted, reactive, ref, watch } from 'vue';
import { teamService } from '@/services/team.service';
import { type DataTableColumns, type DataTableCreateSummary } from 'naive-ui';
import type {
  InitialGrade,
  PresentationGrade,
  Team,
  WorkGrade,
  SubGrade,
} from '@/services/types';
import { useUserStore } from '@/stores/useUserStore';
import { projectGradeService } from '@/services/projectGrade.service';
import DetailsNotes from '@/views/Teams/component/DetailsNotes.vue';
import GenderIcon from '@/components/GenderIcon.vue';
import { toast } from 'vue3-toastify';
import CommFeedMessageIcon from '@/views/CommentsFeedbacks/components/CommFeedMessageIcon.vue';

type Data = {
  id: number;
  validated: boolean;
  name: string;
  gender: string;
  initialGrade: InitialGrade;
  bonusMalusE: number;
  bonusMalusP: number;
  workGrade: WorkGrade;
  presentationGrade: PresentationGrade;
  finalGrade: number;
  details: AllSubGrades;
};

type AllSubGrades = {
  spco: SubGrade;
  prmo: SubGrade;
  teso: SubGrade;
  supr: SubGrade;
  tebm: SubGrade;
  ssbm: SubGrade;
  sspr: SubGrade;
  otpr: SubGrade;
  tcpr: SubGrade;
};

// Définition des props avec valeurs par défaut
const props = withDefaults(
  defineProps<{
    selectedTab: number;
    selectedSprintId: number;
    setLoading: (state: boolean) => void;
  }>(),
  {
    selectedTab: 1,
    selectedSprintId: 0,
  }
);

const teamData = ref<Team[]>([]);
const user = useUserStore().getUser();
const data = ref<Data[]>([]);
const columns = ref<DataTableColumns<Data>>([]);
const isCollapsed = ref(true);
const commentOpen = ref(false);

// Mettre à jour les données en fonction des équipes récupérées
const updateDataFromTeams = async () => {
  const selectedTeam = teamData.value[props.selectedTab];
  data.value = []; // Clear the data array before populating

  if (selectedTeam && props.selectedSprintId !== 0) {
    const sortedUsers = selectedTeam.users.sort((a, b) => {
      const nameA = `${a.lastName}`.toUpperCase();
      const nameB = `${b.lastName}`.toUpperCase();
      const firstNameA = `${a.firstName}`.toUpperCase();
      const firstNameB = `${b.firstName}`.toUpperCase();
      if (nameA < nameB) return -1;
      if (nameA > nameB) return 1;
      return firstNameA < firstNameB ? -1 : 1;
    });

    const promises = sortedUsers.map(async (member) => {
      const [projectGrade] = await Promise.all([
        projectGradeService.updateProjectGrade(
          member.id ?? 0,
          props.selectedSprintId
        ),
      ]);

      return {
        id: projectGrade.id,
        validated: projectGrade.validated,
        name: `${member.firstName} ${member.lastName}`,
        gender: member.gender === 'male' ? 'M' : 'F',
        initialGrade: projectGrade.initialGrade,
        bonusMalusE: projectGrade.workGrade.tebm?.value,
        bonusMalusP: projectGrade.workGrade.ssbm?.value, // Adjust as needed
        workGrade: projectGrade.workGrade,
        presentationGrade: projectGrade.presentationGrade,
        finalGrade: projectGrade.value,
        details: {
          spco: projectGrade.initialGrade.spco,
          prmo: projectGrade.initialGrade.prmo,
          teso: projectGrade.initialGrade.teso,
          supr: projectGrade.initialGrade.supr,
          ssbm: projectGrade.workGrade.ssbm,
          tebm: projectGrade.workGrade.tebm,
          sspr: projectGrade.presentationGrade.sspr,
          otpr: projectGrade.presentationGrade.otpr,
          tcpr: projectGrade.presentationGrade.tcpr,
        },
      };
    });

    data.value = await Promise.all(promises);
    console.log('Data:', data.value);
    updateNotesStatus();
  } else {
    data.value = [];
  }
};

// Création d'une version formatée de data
const formattedData = computed(() => {
  return data.value.map((item) => ({
    ...item,
    id: item.id,
    initialGrade: `${item.initialGrade.value?.toFixed(2)}/20`,
    bonusMalusE: `${item.bonusMalusE?.toFixed(2)}/4`,
    bonusMalusP: `${item.bonusMalusP?.toFixed(2)}/20`,
    workGrade: `${item.workGrade.value?.toFixed(2)}/20`,
    presentationGrade: `${item.presentationGrade.value?.toFixed(2)}/20`,
    finalGrade: `${item.finalGrade?.toFixed(2)}/20`,
  }));
});

// Récupération des données des équipes
const fetchTeamData = async () => {
  const teams = await teamService.getAll();
  if (teams) {
    teamData.value = teams;
    await updateDataFromTeams(); // Await the updated function
    columns.value = createColumns(); // Update columns after data is fetched
  }
};

// Vérifie si l'utilisateur appartient à une équipe
const isUserInTeam = (team: Team) => {
  return user && team.users.some((member) => member.id === user.id);
};

// Création des colonnes de la table
const createColumns = (): DataTableColumns<Data> => {
  if (
    (teamData.value.length > props.selectedTab &&
      isUserInTeam(teamData.value[props.selectedTab]) &&
      status.value) ||
    user?.roles.includes('SS') ||
    user?.roles.includes('OL') ||
    user?.roles.includes('PL') ||
    user?.roles.includes('TC')
  ) {
    return [
      {
        title: 'Nom - Prénom',
        key: 'name',
        align: 'center',
      },
      {
        title: 'M - F',
        key: 'gender',
        align: 'center',
        render: (rowData) => {
          return h(GenderIcon, { gender: rowData.gender });
        },
      },
      {
        title: 'Note initiale',
        key: 'initialGrade',
        align: 'center',
      },
      {
        title: "Bonus / Malus d'équipe",
        width: '11.4%',
        key: 'bonusMalusE',
        align: 'center',
      },
      {
        title: "Bonus / Malus de l'encadrant",
        width: '10.8%',
        key: 'bonusMalusP',
        align: 'center',
      },
      {
        title: 'Note de travail',
        key: 'workGrade',
        align: 'center',
      },
      {
        title: 'Note de présentation',
        key: 'presentationGrade',
        align: 'center',
      },
      {
        title: 'Note du sprint',
        key: 'finalGrade',
        align: 'center',
      },
      {
        type: 'expand',
        renderExpand: (rowData) => {
          return h('div', { class: 'expanded-row' }, [
            h(DetailsNotes, { details: rowData.details }),
          ]);
        },
      },
    ];
  } else {
    return [
      {
        title: 'Nom - Prénom',
        key: 'name',
        align: 'center',
      },
      {
        title: 'M - F',
        key: 'gender',
        align: 'center',
        render: (rowData) => {
          return h(GenderIcon, { gender: rowData.gender });
        },
      },
    ];
  }
};
const validateNotes = async () => {
  const results = [];
  for (const projectGrade of data.value) {
    const result = await projectGradeService.validateProjectGrade(
      projectGrade.id
    );
    results.push(result);
  }
  if (results.includes(false)) {
    toast.error('Une erreur est survenue lors de la validation des notes');
  } else {
    toast.success('Les notes ont été validées avec succès');
    data.value.forEach((item) => {
      item.validated = true;
    });
    updateNotesStatus();
  }
};

// Propriétés réactives pour l'icône et la couleur
const status = ref();
const updateNotesStatus = () => {
  status.value = data.value.every((item) => item.validated);
};

const createSummary = computed<DataTableCreateSummary>(
  () => (pageData: any) => {
    if (user?.roles.includes('OS')) {
      return {
        name: {
          value: null,
          colSpan: 1,
          rowSpan: 1,
        },
        finalGrade: {
          value: null,
          colSpan: 1,
          rowSpan: 1,
        },
      };
    }
    let averageFinalGrade = 'XX/20';

    if (data.value.length > 0) {
      const sum = data.value.reduce(
        (total, current) => total + Number(current.finalGrade),
        0
      );
      const average = sum / data.value.length;
      averageFinalGrade = `${average.toFixed(2)}/20`;
    }

    return {
      name: {
        value: h(
          'span',
          { style: { fontWeight: 'bold', color: 'black' } },
          'Moyenne générale'
        ),
        colSpan: 1,
        rowSpan: 1,
      },
      finalGrade: {
        value: h(
          'span',
          { style: { fontWeight: 'bold', color: 'black' } },
          averageFinalGrade
        ),
        colSpan: 1,
        rowSpan: 1,
      },
    };
  }
);

watch(
  () => [props.selectedTab, props.selectedSprintId],
  () => {
    props.setLoading(true);
    fetchAll().finally(() => props.setLoading(false));
  }
);

const exportGrades = async () => {
  const allData = await Promise.all(
    teamData.value.map(async (team) => {
      return await Promise.all(
        team.users.map(async (userInfo) => {
          const projectGrade = await projectGradeService.updateProjectGrade(
            userInfo.id ?? 0,
            props.selectedSprintId
          );
          return {
            team: team.name,
            name: userInfo.firstName + ' ' + userInfo.lastName,
            gender: userInfo.gender === 'male' ? 'M' : 'F',
            initialGrade: projectGrade.initialGrade.value,
            bonusMalusE: projectGrade.workGrade.tebm?.value,
            bonusMalusP: projectGrade.workGrade.ssbm?.value,
            workGrade: projectGrade.workGrade.value,
            presentationGrade: projectGrade.presentationGrade.value,
            finalGrade: projectGrade.value,
          };
        })
      );
    })
  );

  const data = allData.flat();

  //Crée CSV
  let csv = '\uFEFF';
  csv +=
    'Equipe,Nom,Genre,Note initiale,Bonus/Malus (Eleves),Bonus/Malus (Prof),Note de travail,Note de présentation,Note finale\n';
  data.forEach((row) => {
    csv += `${row.team},${row.name},${row.gender},${row.initialGrade},${row.bonusMalusE},${row.bonusMalusP},${row.workGrade},${row.presentationGrade},${row.finalGrade}\n`;
  });

  let csvBlob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  let csvUrl = URL.createObjectURL(csvBlob);
  let hiddenElement = document.createElement('a');
  hiddenElement.href = csvUrl;
  hiddenElement.target = '_blank';
  hiddenElement.download =
    'notesIndividuelles_Sprint' + props.selectedSprintId + '.csv';
  hiddenElement.click();
};

const publishTeam = async () => {
  try {
    const team = teamData.value[props.selectedTab];
    team.status = 'publish';
    const result = await teamService.saveTeam(team);
    console.log('Team', team);
    console.log('Result:', result);
    toast.success("L'équipe a été validée avec succès");
  } catch (error) {
    toast.error("Une erreur est survenue lors de la validation de l'équipe");
  }
};

const fetchAll = async () => {
  await fetchTeamData().finally(() => props.setLoading(false));
};

onMounted(async () => {
  await fetchAll();
});

const paginationReactive = reactive({
  page: 1,
  pageSize: 8,
  showSizePicker: true,
  pageSizes: [2, 4, 6, 8],
  onChange: (page: number) => {
    paginationReactive.page = page;
  },
  onUpdatePageSize: (pageSize: number) => {
    paginationReactive.pageSize = pageSize;
    paginationReactive.page = 1;
  },
});
</script>

<template>
  <div class="component-container">
    <div class="table-and-info">
      <div class="info-header">
        <div class="teamInfo">
          <h2 class="teamName">
            {{ teamData[props.selectedTab]?.name }}
          </h2>
          <h4 class="profName">
            Professeur référent :
            {{ teamData[props.selectedTab]?.supervisor?.firstName }}
            {{ teamData[props.selectedTab]?.supervisor?.lastName }}
          </h4>
          <h4 class="small-text">
            {{
              status
                ? 'Les notes de sprint ont été validées par le référent'
                : "Les notes de sprint n'ont pas encore été validées"
            }}
          </h4>
        </div>
      </div>
      <div
        class="table-container"
        :style="{ width: data.length ? '' : '500px' }">
        <n-data-table
          :columns="columns"
          :data="formattedData"
          :row-key="(item: any) => item.id"
          :summary="createSummary"
          :pagination="paginationReactive"
          bordered />
      </div>
      <div class="footer-button">
        <n-button
          v-if="
            user?.roles.includes('SS') && teamData[props.selectedTab]?.validated
          "
          @click="validateNotes"
          type="primary"
          class="validateButton"
          title="Validation des notes individuelles">
          Valider les notes
        </n-button>
        <n-button
          v-if="
            teamData.length > props.selectedTab &&
            teamData[props.selectedTab].status === 'prepublish' &&
            teamData[props.selectedTab].supervisor.id === user?.id
          "
          type="primary"
          class="validateButton"
          @click="publishTeam">
          Valider mon équipe
        </n-button>
        <n-button
          v-if="user?.roles.includes('OL') || user?.roles.includes('PL')"
          @click="exportGrades"
          type="info"
          class="validateButton"
          title="Exportation des notes individuelles">
          <i class="fa-solid fa-file-export" />
          <p style="margin-left: 5px">
            {{ user?.roles.includes('OS') ? '' : 'Exporter' }}
          </p>
        </n-button>
      </div>
    </div>

    <n-layout has-sider sider-placement="right">
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
  </div>
</template>

<style scoped>
.info-header {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  width: 100%;
}
.teamInfo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.component-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 30px;
}
.table-and-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.table-container {
  min-width: 700px;
  margin: 20px auto auto;
}
.table-container
  .n-data-table
  >>> .n-data-table-tr--expanded
  > td.n-data-table-td--last-col[colspan='9'] {
  background-color: var(--onBackground) !important;
}

.validateButton {
  align-self: center;
  align-items: center;
  margin-left: 10px;
}
.small-text {
  font-style: italic;
  color: var(--secondaryTextColor);
  font-size: 1em;
}
.footer-button {
  margin-top: 30px;
}
</style>
