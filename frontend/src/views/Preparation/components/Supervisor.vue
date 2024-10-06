<script setup lang="ts">
import { teamService } from '@/services/team.service';
import type { User } from '@/services/types';
import { userService } from '@/services/user.service';
import {
  usePreparationStore,
  type TeamsDTO,
} from '@/stores/usePreparationStore';
import { computed, onMounted, ref, toRaw } from 'vue';
import { toast } from 'vue3-toastify';

type Props = {
  teamNb: number;
  femalePerTeam: number;
  onValidate: () => void;
};
const props = defineProps<Props>();

type TeamForm = {
  name: string;
  supervisorId?: number;
};

const preparationStore = usePreparationStore();

const teams = ref<TeamForm[]>([]);
for (let i = 0; i < props.teamNb; i++) {
  teams.value.push({ name: '', supervisorId: undefined });
}
const supervisorList = ref<User[]>([]);
const supervisorOptions = ref();
const loading = ref(false);

onMounted(async () => {
  const list = await userService.getTeachers();
  supervisorList.value = list;
  supervisorOptions.value = supervisorList.value.map((user) => ({
    label: `${user.firstName} ${user.lastName}`,
    value: user.id,
  }));
});

const ValidateForm = () => {
  for (let i = 0; i < teams.value.length; i++) {
    if (!teams.value[i].name || teams.value[i].supervisorId === undefined) {
      toast.error("Remplissez le champ de l'équipe " + (i + 1) + '.');
      return false;
    }
  }
  return true;
};

const getSelectedSupervisors = () => {
  // Create an array to store the selected supervisors
  const selectedSupervisors = [];

  // Loop through each team and add the selected supervisor to the array
  for (const element of teams.value) {
    const supervisorId = element.supervisorId;
    const supervisor = supervisorList.value.find(
      (user) => user.id === supervisorId
    );
    if (supervisor) {
      selectedSupervisors.push(toRaw(supervisor));
    }
  }

  // Return the array of selected supervisors
  return selectedSupervisors;
};

type SupervisorOption = {
  label: string;
  value: number;
};

const filteredSupervisorOptions = computed(() => (index: number) => {
  const selectedSupervisorIds = teams.value
    .map((team, i) => i !== index && team.supervisorId)
    .filter(Boolean) as number[];
  return supervisorOptions.value
    ? (supervisorOptions.value as SupervisorOption[]).filter(
        (option: SupervisorOption) => !selectedSupervisorIds.includes(option.value)
      )
    : [];
});

const getEnteredNames = () => {
  const names = [];
  for (const element of teams.value) {
    names.push(element.name);
  }
  return names;
};

const handleValidation = async () => {
  if (ValidateForm()) {
    const teamsData: TeamsDTO = {
      students: toRaw(preparationStore.getStudents()),
      teachers: getSelectedSupervisors(),
      status: 'none',
      names: getEnteredNames(),
      numberOfTeams: props.teamNb,
      numberOfGirlsPerTeam: props.femalePerTeam,
    };
    console.log(teamsData);

    loading.value = true;
    const teams = await teamService.createTeams(teamsData);
    if (teams) {
      preparationStore.addTeams(teams);
      props.onValidate();
    } else {
      toast.error('Erreur lors de la création des équipes.');
    }
    loading.value = false;
  }
};
</script>

<template>
  <div class="supervisor-container">
    <p class="message">Vous avez choisi de créer {{ teamNb ?? 0 }} équipes.</p>
    <div class="teams">
      <div class="team" v-for="(team, index) in teams" :key="index">
        <p class="title">Équipe {{ index + 1 }}</p>
        <n-input
          v-model:value="team.name"
          type="text"
          placeholder="Nom de l'équipe" />
        <n-select
          v-model:value="team.supervisorId"
          filterable
          placeholder="Sélectionnez un encadrant"
          :options="filteredSupervisorOptions(index)" />
      </div>
    </div>
    <div class="button-container">
      <n-button type="primary" :loading="loading" @click="handleValidation"
        >Créer les équipes</n-button
      >
    </div>
  </div>
</template>

<style scoped>
.supervisor-container {
  margin-top: 100px;
  width: 100%;
  max-width: 1000px;
}
.message {
  color: var(--textColor);
  font-weight: 500;
  margin-bottom: 20px;
  font-size: 1rem;
}
.teams {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin: 0 auto;
}
.team {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}
.title {
  color: var(--textColor);
}
.button-container {
  display: flex;
  justify-content: center;
  margin: 0 auto;
  margin-top: 50px;
}
</style>
