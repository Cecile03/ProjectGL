<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { sprintService, type Sprint } from '../services/sprint.service';
import { useUserStore } from '@/stores/useUserStore';
import { CalendarLtr28Regular as Calendar } from '@vicons/fluent';
import { formatDateToDDMMYY } from '@/utils/utils';

type Props = {
  size?: 'small' | 'medium' | 'large';
};
defineProps<Props>();

const userStore = useUserStore();
const sprints = ref<Sprint[] | null>(null);
const options = ref<
  | {
      label: string;
      value: number;
    }[]
  | undefined
>(undefined);
const selectedSprint = ref<number | null>(null);
const selectedSprintDate = ref<{ start?: string; end?: string }>({});

onMounted(async () => {
  const res = await sprintService.getAllSprints();
  if (res) {
    sprints.value = res;
    const currentDate = new Date();
    const currentSprint = res.find((sprint) => {
      const startDate = new Date(sprint.startDate); // Assurez-vous que startDate est au format correct
      const endDate = new Date(sprint.endDate); // Assurez-vous que endDate est au format correct
      return currentDate >= startDate && currentDate <= endDate;
    });

    if (currentSprint) {
      selectedSprint.value = currentSprint.id;
    } else {
      selectedSprint.value = res[0].id;
    }

    setSelectedSprintDates();
    options.value = res?.map((s, index) => ({
      label: `Sprint ${index + 1}`,
      value: s.id,
    }));
  }
});

const setSelectedSprintDates = () => {
  if (!sprints.value) return;

  const selectedSprintData = sprints.value.find(
    (s) => s.id == selectedSprint.value
  );
  if (selectedSprintData) {
    selectedSprintDate.value.start = formatDateToDDMMYY(
      selectedSprintData.startDate
    );
    selectedSprintDate.value.end = formatDateToDDMMYY(
      selectedSprintData.endDate
    );
  }
};

watch(selectedSprint, (newValue, oldValue) => {
  if (newValue && newValue !== oldValue) {
    userStore.setSelectedSprintId(newValue);
    setSelectedSprintDates();
  }
});
</script>

<template>
  <div class="sprintSelector">
    <n-select
      v-if="!sprints"
      class="selector"
      size="small"
      value="Pas de sprint."
      disabled />
    <n-select
      v-else
      class="selector"
      :size="size ? size : 'small'"
      v-model:value="selectedSprint"
      :options="options" />
    <div class="sprintDate" v-show="sprints">
      <n-icon class="icon" :component="Calendar" :size="18"></n-icon>
      <p>
        Du <span class="date">{{ selectedSprintDate.start }}</span> au
        <span class="date">{{ selectedSprintDate.end }}</span>
      </p>
    </div>
  </div>
</template>

<style scoped>
.selector {
  width: 200px;
}
.sprintDate {
  display: flex;
  align-items: center;
  font-size: 0.8rem;
  color: var(--secondaryTextColor);
  font-weight: 500;
  text-align: center;
  width: 100%;
  margin-top: 4px;
}
.sprintDate .icon {
  color: var(--primaryColor);
  margin-right: 5px;
  margin-bottom: 2px; /* Calendar icon looks more centered with 2px margin */
}
.sprintDate .date {
  color: var(--primaryColor);
  font-weight: 600;
}
</style>
