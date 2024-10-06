<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { userService } from '@/services/user.service';
import { teamService } from '@/services/team.service';
import { usePreparationStore } from '@/stores/usePreparationStore';
import Loading from '@/components/Loading.vue';
import { sprintService } from '@/services/sprint.service';

const STEPS = [
  {
    title: 'Créer une liste',
    description:
      'Créez une nouvelle liste d’étudiants à partir d’un fichier CSV.',
  },
  {
    title: 'Importer des étudiants',
    description: 'Importez un fichier CSV avec les informations des étudiants.',
  },
  {
    title: 'Configurer les équipes',
    description:
      'Définissez le nombre d’équipes et attribuez un professeur à chacune.',
  },
  {
    title: 'Visualiser les équipes',
    description: 'Prévisualisez les équipes générées selon vos critères.',
  },
  {
    title: 'Créer les sprints',
    description: 'Définissez les étapes clés du projet avec des sprints.',
  },
  {
    title: 'Établir les barèmes',
    description:
      'Créez des barèmes de notes pour évaluer selon divers critères.',
  },
];
const selectedStep = ref<number>(0);
const currentStep = ref(0);

const getTabIndex = (path?: string) => {
  if (path === 'create') return 1;
  if (path === 'import') return 2;
  if (path === 'config') return 3;
  if (path === 'preview') return 4;
  if (path === 'sprint') return 5;
  if (path === 'gradeScale') return 6;
  return 1;
};

const getPath = (tabIndex: number) => {
  if (tabIndex === 1) return 'create';
  if (tabIndex === 2) return 'import';
  if (tabIndex === 3) return 'config';
  if (tabIndex === 4) return 'preview';
  if (tabIndex === 5) return 'sprint';
  if (tabIndex === 6) return 'gradeScale';
  return 'create';
};

const navigate = useRouter().push;
const preparationStore = usePreparationStore();
const loadingData = ref(false);

const setMaxView = (viewPath: string) => {
  currentStep.value = getTabIndex(viewPath);
  selectedStep.value = getTabIndex(viewPath);
  navigate(`/preparation/${viewPath}`);
};

const fetchSprints = async () => {
  const sprints = await sprintService.getAllSprints();
  return sprints && sprints.length > 0;
};

const fetchTeams = async () => {
  const teams = await teamService.getAll();
  if (teams) preparationStore.addTeams(teams);
  return !!teams;
};

const fetchStudents = async () => {
  const studentList = await userService.getStudents();
  if (studentList) preparationStore.addStudentList(studentList);
  return !!studentList;
};

onMounted(async () => {
  console.log('preparation onMounted');
  loadingData.value = true;

  if (!(await fetchStudents())) setMaxView('import');
  else if (!(await fetchTeams())) setMaxView('config');
  else if (!(await fetchSprints())) setMaxView('preview');
  else setMaxView('gradeScale');

  loadingData.value = false;
});

const handleValidation = () => {
  const currentPath = window.location.pathname;
  const tabName = currentPath.substring(currentPath.lastIndexOf('/') + 1);
  const nextTab = getTabIndex(tabName) + 1;
  if (nextTab < STEPS.length + 1) {
    setMaxView(getPath(nextTab));
  }
};
</script>

<template>
  <div class="preparationView">
    <div class="progressbar">
      <n-steps
        v-model:current="selectedStep"
        size="small"
        :on-update:current="
          (index: number) => {
            selectedStep = index;
            navigate('/preparation/' + getPath(index));
          }
        ">
        <n-step
          v-for="(step, index) in STEPS"
          :key="index"
          :title="step.title"
          :description="step.description"
          :disabled="index + 1 > currentStep" />
        <!-- index + 1 because naive set index start at 1 -->
      </n-steps>
    </div>

    <div class="wrapper">
      <Loading v-if="loadingData" />
      <RouterView v-else @validate="handleValidation" />
    </div>
  </div>
</template>
<style scoped>
.progressbar {
  position: relative;
  width: 90%;
  min-height: 140px;
  padding: 10px;
  overflow-x: scroll;
  margin: auto;
  margin-top: 100px;
}
.progressbar::after {
  position: absolute;
  content: '';
  height: 1px;
  background-color: var(--borderColor);
  width: 90%;
  right: 50%;
  bottom: 0px;
  box-shadow: 0px 0px 10px 0px #c2c2c2c4;
  transform: translateX(50%);
}
.preparationView .wrapper {
  padding: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
