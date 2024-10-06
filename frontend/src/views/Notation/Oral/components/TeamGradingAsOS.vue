<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { AxiosClient } from '@/services';
import { useUserStore } from '@/stores/useUserStore';
import type { User } from '@/services/types';
import GradeScaleInfoIcon from '@/components/GradeScaleInfoIcon.vue';
import { sprintService } from '@/services/sprint.service';
import { teamGradeFromStudentService } from '@/services/teamGradeFromStudent.service';
import Loading from '@/components/Loading.vue';
import { toast } from 'vue3-toastify';
import SprintSelector from '@/components/SprintSelector.vue';

const userStore = useUserStore();

const userInfo = ref<User | null>(null);
userInfo.value = userStore.getUser();

const grades = reactive<number[]>([]);

const teamsCount = ref(0);

const selectedSprint = computed(() =>
  useUserStore().getSelectedSprintId()
);

type Team = {
  id: number;
  name: string;
  users: User[];
};

const otherTeams = ref<Team[]>([]);
const myTeam = ref<Team>();

const handleValidate = async () => {
  // Update the grade for each team
  let allSavedSuccessfully = true;
  for (const [index, team] of otherTeams.value.entries()) {
    const grade = grades[index];
    if (myTeam.value?.id === undefined) {
      console.error('My team is undefined');
      return;
    }
    const saveSuccessful = await teamGradeFromStudentService.saveTeamGradeFromStudent(
      myTeam.value?.id,
      team.id,
      selectedSprint.value ?? 0,
      grade
    );
    if (!saveSuccessful) {
      allSavedSuccessfully = false;
    }
  }
  if (allSavedSuccessfully) {
    toast.success('Les notes ont été sauvegardées avec succès');
  } else {
    toast.error('Erreur lors de la sauvegarde des notes');
  }
};

const fetchNotes = async () => {
  await updateIsSprintActive();
  for (const [index, team] of otherTeams.value.entries()) {
    try {
      if (myTeam.value?.id === undefined) {
        console.error('My team is undefined');
        return;
      }
      const response =
        await teamGradeFromStudentService.getTeamOrderByTeamNotingIdAndTeamToNoteIdAndSprintId(
          myTeam.value?.id,
          team.id,
          selectedSprint.value ?? 0
        );
      grades[index] = response?.grade;
    } catch (error) {
      grades[index] = 0;
    }
  }
};

const isSprintActive = ref(false);

const updateIsSprintActive = async () => {
  const today = new Date();
  const selectedSprintData = await sprintService.getSprintById(selectedSprint.value ?? 0);
  isSprintActive.value = (
    selectedSprintData &&
    today >= selectedSprintData.startDate &&
    today <= selectedSprintData.endDate
  ) ?? false;
};

watch(selectedSprint, fetchNotes);

const fetchAll = async () => {
  try {
    const response = await AxiosClient.get('/teams');
    const teams = response.data;

    // Suppose activeUserId contains the ID of the active user
    const activeUserId = userInfo.value?.id;

    // Filter out the active user's team
    otherTeams.value = teams.filter(
      (team: any) => !team.users.some((user: any) => user.id === activeUserId)
    );
    myTeam.value = teams.find((team: any) =>
      team.users.some((user: any) => user.id === activeUserId)
    );

    teamsCount.value = otherTeams.value.length;

    await fetchNotes();
  } catch (error) {
    console.error(error);
  }
};

const pageNotReady = ref(true);

onMounted(async () => {
  await fetchAll().finally(() => {
    pageNotReady.value = false;
  });
});
</script>

<template>
  <Loading v-if="pageNotReady" />
  <div v-else >
    <Breadcrumb />
    <div class="container">
    <h1>Notation des équipes pour la présentation</h1>
    <div class="select-container">
      <SprintSelector class="team-select"/>
      <GradeScaleInfoIcon class="grade-icon"/>
    </div>
    <n-card :bordered="false" class="gradeInputs">
      <div
        v-for="(team, index) in otherTeams"
        :key="index"
        class="team-grade-row"
      >
        <div class="team-name">{{ team.name }}</div>
        <n-input-number
          class="grade-input"
          :min="0"
          :max="20"
          v-model:value="grades[index]"
          clearable
          :disabled="!isSprintActive"></n-input-number>
      </div>
      <div class="button-container">
        <n-button @click="handleValidate">Valider</n-button>
      </div>
    </n-card>
    </div>
  </div>
</template>


<style scoped>
h1 {
  text-align: center;
  margin-top: 3%;
  font-size: 1.8em;
}

.gradeInputs {
  width: 55%;
  margin: 0 auto;
}

.team-grade-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #eaeaea;
}

.team-name {
  flex: 1;
  font-size: 1.2em;
}

.grade-input {
  display: flex;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  margin-top: 10px;
}

.team-select {
  max-width: 300px;
  margin-left: 10px;
}

.container {
  padding: 20px;
}
</style>
