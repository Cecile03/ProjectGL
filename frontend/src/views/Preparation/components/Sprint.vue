<template>
  <Loading v-if="pageNotReady" />
  <div v-else class="sprintView">
    <div class="select">
      <h1 class="title">Sprint protocol</h1>
      <div class="sprint sprint_title">
        <p class="p_type">Nombre de sprint :</p>
        <div class="input">
          <n-input-number
            class="input_sprint_number"
            v-model:value="sprintNumber"
            @update:value="createSprints"
            size="medium"
            placeholder=""
            :disabled="sprintsCreated"
            :min="0" />
        </div>
      </div>
    </div>
    <div class="display">
      <div class="sprintDate" v-show="showSprintDate">
        <div class="sprint_date">
          <div class="sprintN" v-for="(sprint, n) in sprints" :key="n">
            <h2 class="title2">
              <i
                class="fa-solid fa-users-viewfinder"
                style="margin-right: 5px"></i>
              Sprint {{ n + 1 }}
            </h2>
            <div class="sprint">
              <div class="input">
                <div class="endTypeGr">
                  <label class="label-input" :for="'endType-' + n"
                    ><i class="fas fa-cogs" style="margin-right: 5px"></i> Type
                    de fin :</label
                  >
                  <n-select
                    id="'endType-' + n"
                    class="input_sprint input_sprint_endType"
                    v-model:value="sprint.endType"
                    :options="[
                      { label: 'Presentation', value: 'Presentation' },
                      { label: 'Sprint Review', value: 'Sprint Review' },
                    ]"
                    size="medium"></n-select>
                </div>
                <div class="startDateGr">
                  <label class="label-input" :for="'startDate-' + n"
                    ><i
                      class="fa-regular fa-calendar"
                      style="margin-right: 5px"></i>
                    Date de début :</label
                  >
                  <NDatePicker
                    id="'startDate-' + n"
                    class="input_sprint input_sprint_startDate"
                    v-model:value="sprint.startDate"
                    size="medium"
                    type="date"
                    :format="format"
                    placeholder="Date de début"
                    clearable
                    @update:value="
                      (newDate) => {
                        updateStartDate(n, newDate);
                      }
                    " />
                </div>
                <div class="endDateGr">
                  <label class="label-input" :for="'endDate-' + n"
                    ><i
                      class="fa-regular fa-calendar"
                      style="margin-right: 5px"></i>
                    Date de fin :</label
                  >
                  <NDatePicker
                    id="'endDate-' + n"
                    class="input_sprint input_sprint_endDate"
                    v-model:value="sprint.endDate"
                    size="medium"
                    type="date"
                    :format="format"
                    placeholder="Date de fin"
                    :status="endDateErrorStatus[n] ? 'error' : undefined"
                    clearable
                    :data-status="endDateErrorStatus[n] ? 'error' : 'normal'"
                    @update:value="
                      (newDate) => {
                        updateEndDate(n, newDate);
                      }
                    " />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="button">
          <NButton class="primaryButton primaryButton2" @click="handleClick"
            >Valider</NButton
          >
          <NButton class="errorButton" type="error" @click="clearSprints"
            >Effacer</NButton
          >
          <NButton
            id="nextstep"
            class="secondaryButton"
            @click="handleValidation"
            >Suivant</NButton
          >
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Add this rule to make .select and .display stack vertically */
.sprintView {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.label-input {
  width: 33%;
}

.title2 {
  margin-bottom: 10px;
  margin-top: 5px;
  text-align: center;
}

.display {
  width: 90vw;
}

.input_sprint {
  width: 50%;
}

.select{
  margin-top: 50px;
}

.title {
  margin-top: 15px;
  margin-bottom: 20px;
  text-align: center;
}

.sprint {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  text-align: center;
  align-items: center;
  align-content: center;
}

.input {
  padding-left: 20px;
  width: 100%;
}
.sprint .p_type {
  white-space: nowrap;
}

.p_type{
  margin-left: 30px;
}

.endTypeGr,
.startDateGr,
.endDateGr {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px; /* Ajoute un espace entre le label et l'entrée */
}

.sprint_date {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* Responsive grid */
  gap: 20px;
  padding-top: 20px;
}

/* Add this rule to make the tables take up almost the full width of their grid cell */
.sprint_date .sprintN {
  width: 100%;
  padding: 10px 10px; /* Ajustez au besoin */
  border: 0.1px solid var(--borderColor);
  box-shadow: 0 0 10px 0 #d8d8d873;
  border-radius: 10px; /* Angles arrondis */
}

.errorButton {
  background-color: var(--redColor);
  margin: 20px 0;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.primaryButton {
  margin: 20px 0;
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.button {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 20px;
}

.primaryButton:hover {
  background-color: #0056b3;
}

.secondaryButton {
  margin: 20px 0;
  color: #007bff;
  background-color: white;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.secondaryButton:hover {
  background-color: #0056b3;
}
</style>

<script setup lang="ts">
import { NInputNumber, NButton, NDatePicker, NSelect } from 'naive-ui';
import { onMounted, ref, watch } from 'vue';
import { sprintService } from '@/services/sprint.service';
import Loading from '@/components/Loading.vue';
import { toast } from 'vue3-toastify';

// Define the emits
const emit = defineEmits(['validate']);
const pageNotReady = ref(true);

// Define data properties for the sprint object
interface Sprint {
  id: number;
  startDate: Date | null;
  endDate: Date | null;
  endType: string;
}

const sprintNumber = ref(0);
const showSprintDate = ref(false);
const format = 'dd/MM/yyyy';
const sprints = ref<Sprint[]>([]);
const endDateErrorStatus = ref<boolean[]>([]);
const sprintsCreated = ref<boolean>(false);

const handleValidation = async () => {
  emit('validate');
};

const updateStartDate = (index: number, newDate: Date | number | null) => {
  if (typeof newDate === 'number') {
    newDate = new Date(newDate);
  }
  const previousSprintEndDate =
    index > 0 ? sprints.value[index - 1].endDate : null;
  if (previousSprintEndDate && newDate && newDate <= previousSprintEndDate) {
    toast.warning(
      'La date de début doit être après la date de fin du sprint précédent'
    );
    newDate = new Date(previousSprintEndDate.getTime() + 24 * 60 * 60 * 1000); // J+1
  }
  sprints.value[index].startDate = newDate;
};

const updateEndDate = (index: number, newDate: Date | number | null) => {
  if (typeof newDate === 'number') {
    newDate = new Date(newDate);
  }
  const startDate = sprints.value[index].startDate;
  const nextSprintStartDate =
    index < sprints.value.length - 1
      ? sprints.value[index + 1].startDate
      : null;
  if (startDate && newDate && newDate < startDate) {
    toast.warning(
      'La date de fin ne peut pas être antérieure à la date de début'
    );
    endDateErrorStatus.value[index] = true;
    newDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000); // J+1
  } else if (nextSprintStartDate && newDate && newDate >= nextSprintStartDate) {
    toast.warning(
      'La date de fin doit être avant la date de début du sprint suivant'
    );
    endDateErrorStatus.value[index] = true;
    newDate = new Date(nextSprintStartDate.getTime() - 24 * 60 * 60 * 1000); // J-1
  } else {
    endDateErrorStatus.value[index] = false;
  }
  sprints.value[index].endDate = newDate;
};

const createSprints = () => {
  // Only create sprints if there are no sprints already displayed
  if (!sprintsCreated.value) {
    showSprintDate.value = true;
    sprints.value = Array.from({ length: sprintNumber.value }, () => ({
      id: -1,
      startDate: null,
      endDate: null,
      endType: '',
    }));
  }
};

const handleClick = async () => {
  try {
    for (let i = 0; i < sprints.value.length; i++) {
      const sprint = sprints.value[i];
      console.log(sprint);
      await sprintService.updateSprint({
        id: sprint.id,
        startDate: sprint.startDate ? sprint.startDate : new Date(),
        endDate: sprint.endDate ? sprint.endDate : new Date(),
        endType: sprint.endType,
      });
    }
    toast.success(`Sprints créés avec succès`);
    sprintsCreated.value = true;
  } catch (error) {
    toast.error(`Erreur lors de la création des sprints`);
  }
};

const clearSprints = () => {
  sprintService.deleteAll();
  sprints.value = [];
  showSprintDate.value = false;
  sprintsCreated.value = false;
  sprintNumber.value = 0;
};

const printBool = () => {
  console.log(sprintsCreated.value);
};

const fetchSprints = async () => {
  try {
    const response = await sprintService.getAllSprints();
    console.log(response);
    if (response && Array.isArray(response)) {
      sprints.value = response.map((sprint) => ({
        id: sprint.id,
        startDate: new Date(sprint.startDate),
        endDate: new Date(sprint.endDate),
        endType: sprint.endType,
      }));
      console.log(sprints.value);
      sprintNumber.value = sprints.value.length;
      if (sprintNumber.value > 0) {
        sprintsCreated.value = true;
      }
      showSprintDate.value = true;
    }
  } catch (error) {
    toast.error("Erreur lors de l'affichage des sprints");
  }
};

watch(sprintsCreated, printBool);

onMounted(async () => {
  await fetchSprints().finally(() => (pageNotReady.value = false));
});
</script>
