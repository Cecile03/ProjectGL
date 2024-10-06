<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Criteria from './Criteria.vue';
import Supervisor from './Supervisor.vue';
import { usePreparationStore } from '@/stores/usePreparationStore';
import type { Criteria as CriteriaType } from '@/services/types';
import { teamService } from '@/services/team.service';
import { useMessage } from 'naive-ui';

const preparationStore = usePreparationStore();
const message = useMessage();
const emits = defineEmits(['validate']);

const teamNb = ref(0);
const ratioMF = ref(0);
const isTeamsCreated = ref(false);
const criteria = ref<CriteriaType | null>(null);
const deleteTeamsLoading = ref(false);

onMounted(() => {
  if (preparationStore.getTeams().length) {
    isTeamsCreated.value = true;
    if (preparationStore.getTeams().at(0) !== undefined) {
      criteria.value = preparationStore.getTeams().at(0)!.criteria;
    }
  }
});

const handleCriteriaValidate = (numberOfTeam: number, femaleRatio: number) => {
  console.log('handleCriteriaValidate');

  teamNb.value = numberOfTeam;
  ratioMF.value = femaleRatio;
};

const handleDeleteTeams = () => {
  deleteTeamsLoading.value = true;
  teamService
    .deleteAll()
    .then(() => {
      message.success('Les équipes ont été supprimées.');
      isTeamsCreated.value = false;
    })
    .finally(() => (deleteTeamsLoading.value = false));
};
</script>

<template>
  <div v-if="isTeamsCreated" class="teamsExist">
    <p class="title">
      Les équipes ont déjà été créées sur les critères suivants :
    </p>
    <p class="label">
      Nombre d'équipes :
      <span class="value">{{ criteria?.numberOfTeams }}</span>
    </p>
    <p class="label">
      Nombre de femmes par équipes :
      <span class="value">{{ criteria?.numberOfGirls }}</span>
    </p>
    <div class="btns">
      <n-popconfirm
        :negative-text="null"
        :positive-text="'Confirmer'"
        @positive-click="handleDeleteTeams">
        <template #trigger>
          <n-button type="warning" :loading="deleteTeamsLoading"
            >Recréer des équipes</n-button
          >
        </template>
        En recommançant la procédure de création d'équipes, les équipes
        existantes seront toutes supprimées.
      </n-popconfirm>
    </div>
  </div>
  <template v-else>
    <Criteria v-if="teamNb === 0" :on-next-click="handleCriteriaValidate" />
    <Supervisor
      v-else
      :team-nb="teamNb"
      :female-per-team="ratioMF"
      :on-validate="() => emits('validate')" />
  </template>
</template>

<style scoped>
.teamsExist .title {
  font-size: 1.4rem;
  font-weight: 500;
  margin-top: 100px;
  margin-bottom: 10px;
}
.label {
  font-size: 0.9rem;
  margin: 5px;
}
.value {
  font-size: 0.9rem;
  font-weight: 500;
}
.btns {
  display: flex;
  justify-content: space-evenly;
  gap: 20px;
  margin-top: 40px;
}
</style>
