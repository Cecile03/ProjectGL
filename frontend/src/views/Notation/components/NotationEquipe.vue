<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NInputNumber } from 'naive-ui';
import { detailService } from '@/services/detail.service';
import { categoryService } from '@/services/category.service';
import { gradeScaleService } from '@/services/gradeScale.service';
import { toast } from 'vue3-toastify';
import Loading from './../../../components/Loading.vue';
import { teamGradeService } from '@/services/teamGrade.service';
import { useUserStore } from '@/stores/useUserStore';
import { BookOutline } from '@vicons/ionicons5';

type Props = {
  teamId: number;
  sprintId: number;
  teamName: string;
};

const props = defineProps<Props>();
const sprintId = computed(() => props.sprintId);
const teamId = computed(() => props.teamId);
const pageNotReady = ref(true);
const loadingRefresh = ref(false);

const user = useUserStore().getUser();

type Detail = {
  id: number;
  name: string;
  grade: number | null;
  mark: number;
};
type Category = {
  id: number;
  name: string;
  details: Detail[];
};

const calculateTotal = (details: Detail[]) => {
  let totalGrade = 0;
  let totalMark = 0;
  for (const detail of details) {
    if (detail.grade) {
      totalGrade += detail.grade;
    }
    totalMark += detail.mark;
  }
  return { totalGrade, totalMark };
};

const createColumns = (): DataTableColumns<Detail> => [
  {
    title: 'Note',
    key: 'note',
    render(row) {
      return h(NInputNumber, {
        value: row.grade,
        placeholder: 'Note',
        min: 0,
        max: row.mark,
        'onUpdate:value': (value: number | null) => {
          row.grade = value;
        },
      });
    },
  },
  {
    title: 'Critère',
    key: 'name',
    render(row) {
      return h('p', `${row.name} (/${row.mark})`);
    },
  },
];

const columns = createColumns();
const categories = ref<Category[]>([]);
const gradeScale = ref();

onMounted(async () => {
  await fetchAll();
  pageNotReady.value = false;
});

const fetchAll = async () => {
  if (teamId.value != null) {
    gradeScale.value = await gradeScaleService.getGradeScaleProject();
    console.log('gradeScale', gradeScale.value);
    if (gradeScale.value.id != undefined) {
      const categoriesData = await categoryService.getCategoriesByGradeScaleId(
        gradeScale?.value.id
      );
      for (const category of categoriesData) {
        const detailsData = await detailService.getDetailsByCategoryId(
          category.id
        );
        const details: Detail[] = detailsData.map((detail: any) => ({
          id: detail.id,
          name: detail.name,
          grade: null,
          mark: detail.mark,
        }));

        categories.value.push({
          id: category.id,
          name: category.name,
          details: details,
        });
      }
    }
    await resetTable();
  }
};

const saveGrades = async () => {
  let success = [];
  if (teamId.value != null && sprintId.value != null) {
    for (const category of categories.value) {
      for (const detail of category.details) {
        if (detail.grade != null)
          success.push(
            await teamGradeService.saveTeamGrade(
              teamId.value,
              sprintId.value,
              detail.id,
              user?.id ?? 0,
              detail.grade
            )
          );
      }
    }
  }
  if (success.includes(false)) {
    toast.error(`Une erreur s'est produite lors de la sauvegarde.`);
  } else {
    toast.success(`Les notes ont été sauvegardées avec succès`);
  }
};

const resetTable = async () => {
  if (teamId.value != null && sprintId.value != null) {
    for (const category of categories.value) {
      for (const detail of category.details) {
        try {
          const data = await teamGradeService.getTeamGrade(
            teamId.value,
            sprintId.value,
            detail.id,
            user?.id ?? 0
          );
          console.log(data);
          detail.grade = data?.grade;
        } catch (error) {
          detail.grade = 0;
        }
      }
    }
  }
};

watch([sprintId, teamId], async () => {
  if (sprintId.value != null && !pageNotReady.value) {
    categories.value = [];
    loadingRefresh.value = true;
    await fetchAll();
    loadingRefresh.value = false;
  }
});
</script>

<template>
  <Loading v-if="pageNotReady || loadingRefresh" />
  <Empty
    v-else-if="gradeScale.id == undefined"
    title="Aucun barème"
    subtitle="Il n'y a aucun barème de notation à afficher"
    :icon="BookOutline"
    path="/notation"
    bouton-string="Retour" />
  <n-card v-else class="primary-container">
    <template #header>
      <h2 style="text-align: center; color: var(--textColor)">
        Notation du projet
      </h2>
    </template>
    <div v-if="teamId" class="scales-container">
      <n-card
        class="secondary-container"
        v-for="(category, index) in categories"
        :key="index">
        <h2 class="title">{{ category.name }}</h2>
        <n-data-table :columns="columns" :data="category.details" />
        <h2 class="total">
          <p>Total</p>
          <p>
            {{ calculateTotal(category.details).totalGrade }}/{{
              calculateTotal(category.details).totalMark
            }}
          </p>
        </h2>
      </n-card>
    </div>
    <div v-if="teamId" class="footer">
      <n-button type="primary" @click="saveGrades">Valider</n-button>
    </div>
  </n-card>
</template>

<style scoped>
.footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 25px;
}
.title {
  font-size: 20px;
  text-align: center;
  font-weight: bold;
  background-color: var(--onBackground);
  padding: 10px;
}
.total {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 15px;
  text-align: center;
  font-weight: bold;
  background-color: var(--onBackground);
  padding: 10px;
}
.primary-container {
  padding-left: 75px;
  padding-right: 75px;
  padding-top: 20px;
  border-width: 0;
}
.secondary-container {
  border-width: 0;
  margin: 10px;
}
.secondary-container :deep(.n-card__content) {
  padding: 0;
}
.scales-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
@media screen and (max-width: 1200px) {
  .scales-container {
    flex-wrap: wrap;
  }
  .secondary-container {
    max-width: 400px;
    margin: 10px auto;
  }
}
</style>
