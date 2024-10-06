<script lang="ts" setup>
import { usePreparationStore } from '@/stores/usePreparationStore';
import { onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';

type Props = {
  onNextClick: (numberOfTeam: number, femaleRatio: number) => void;
};
const props = defineProps<Props>();

const preparationStore = usePreparationStore();

const studentStats = ref({ totalNb: 0, femaleNb: 0, maleNb: 0 });
const teamNb = ref(0);
const ratioMF = ref(0);

onMounted(() => {
  const students = preparationStore.getStudents();
  studentStats.value.totalNb = students.length;
  studentStats.value.femaleNb = students.filter(
    (s) => s.gender === 'female'
  ).length;
  studentStats.value.maleNb = students.filter(
    (s) => s.gender === 'male'
  ).length;
});
const validateRatios = () => {
  if (!teamNb.value) return false;
  if (!ratioMF.value) return false;
  return true;
};

const onNextButtonClick = () => {
  if (validateRatios()) {
    props.onNextClick(teamNb.value, ratioMF.value);
  } else {
    toast.error('La valeur ne sont pas valide.');
  }
};
</script>

<template>
  <div class="criteria-container">
    <div class="prev-info">
      <p>
        Les informations suivantes ont été extraites des étapes remplies
        précédement :
      </p>
      <p class="stats">
        Nombre d'étudiants : {{ studentStats.totalNb }} (
        {{ studentStats.femaleNb }} filles,

        {{ studentStats.maleNb }} garçons )
      </p>
    </div>
    <div class="row">
      <p class="label">Nombre d'équipes :</p>
      <n-input-number
        v-model:value="teamNb"
        :min="0"
        :max="studentStats.totalNb"
        :step="1"
        placeholder="Saisissez le nombre d'équipes souhaité">
        <template #suffix v-if="teamNb">
          <p class="suffix">
            {{ Math.floor(studentStats.totalNb / teamNb)
            }}{{
              Math.ceil(studentStats.totalNb / teamNb) !==
              Math.floor(studentStats.totalNb / teamNb)
                ? '-' + Math.ceil(studentStats.totalNb / teamNb)
                : ''
            }}
            étudiants / équipe
          </p>
        </template>
      </n-input-number>
    </div>
    <div class="row">
      <p class="label">Ratio Homme / Femme :</p>
      <div class="ratio">
        <n-input-number
          v-model:value="ratioMF"
          :min="1"
          :max="studentStats.femaleNb"
          :step="1"
          placeholder="Nombre de femmes par équipe">
          <template #suffix v-if="ratioMF">
            <p class="suffix">
              {{ Math.floor(studentStats.femaleNb / ratioMF) }} équipe(s) de
              {{ ratioMF }} fille(s)
            </p>
          </template>
        </n-input-number>
      </div>
    </div>
    <div class="criteria-buttons">
      <n-button @click="onNextButtonClick"> Suivant </n-button>
    </div>
  </div>
</template>

<style>
.criteria-container {
  margin-top: 100px;
  width: 500px;
  display: flex;
  flex-direction: column;
}
.criteria-container .prev-info {
  margin-bottom: 20px;
}
.criteria-container .prev-info .stats {
  font-weight: 500;
  color: var(--textColor);
  margin: 5px 0;
  font-style: italic;
}
.criteria-container .row {
  margin: 15px 0;
}
.criteria-container .row .ratio {
  width: 100%;
}
.criteria-container .row .suffix {
  color: rgba(121, 120, 120, 0.743);
}
.criteria-container .row .label {
  margin-bottom: 4px;
}

.criteria-container .criteria-buttons {
  display: flex;
  justify-content: center;
  gap: 100px;
  margin-top: 50px;
}
</style>
