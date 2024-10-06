<script lang="ts">
import { defineComponent, h, onMounted, ref, watch } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NInputNumber } from 'naive-ui';
import { detailService } from '@/services/detail.service';
import { categoryService } from '@/services/category.service';
import { gradeScaleService } from '@/services/gradeScale.service';
import { toast } from 'vue3-toastify';
import Loading from '@/components/Loading.vue';
import { teamGradeService } from '@/services/teamGrade.service';
import { useUserStore } from '@/stores/useUserStore';
import { BookOutline } from '@vicons/ionicons5';
import Empty from '@/components/Empty.vue';

export default defineComponent({
  computed: {
    FlagOutline() {
      return BookOutline
    }
  },
  components: { Empty, Loading},
  props: {
    teamId: Number,
    sprintId: Number,
  },
  setup(props, { emit }) {
    const teamOptions = ref<{ label: string; value: number }[]>([]);
    const pageNotReady = ref(true);
    const isGradeScaleReady = ref(true);

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
          return `${row.name} (/${row.mark})`;
        },
      },
    ];

    const columns = createColumns();
    const categories = ref<Category[]>([]);    const gradeScale = ref<any>(null);

    const fetchAll = async () => {
      console.log('Équipe sélectionnée:', props.teamId);
      if (props.teamId != null) {
        try {
          gradeScale.value = await gradeScaleService.getGradeScalePresentation();
          if (gradeScale.value != null && gradeScale.value.id != undefined) {
            const categoriesData =
              await categoryService.getCategoriesByGradeScaleId(
                gradeScale.value?.id
              );
            for (const category of categoriesData) {
              console.log('Catégorie:', category)
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
            await resetTable();
          } else {
            isGradeScaleReady.value = false;
          }
        } catch (error) {
          console.error('Erreur lors de la récupération des données :', error);
        }
      }
    };

    const saveGrades = async () => {
      let success = [];
      if (props.teamId != null && props.sprintId != null) {
        for (const category of categories.value) {
          for (const detail of category.details) {
            if (detail.grade != null)
              success.push(
                await teamGradeService.saveTeamGrade(
                  props.teamId,
                  props.sprintId,
                  detail.id,
                  user?.id??0,
                  detail.grade
                )
              );
          }
        }
        if (success.includes(false)) {
          toast.error(`Une erreur s'est produite lors de la sauvegarde.`);
        } else {
          toast.success(`Les notes ont été sauvegardées avec succès`);
        }
      }
    };

    const resetTable = async () => {
      if (props.teamId != null && props.sprintId != null) {
        for (const category of categories.value) {
          for (const detail of category.details) {
            try {
              const data = await teamGradeService.getTeamGrade(
                props.teamId,
                props.sprintId,
                detail.id,
                user?.id??0
              );
              console.log('Données de note:', data);
              detail.grade = data?.grade;
            } catch (error) {
              detail.grade = 0;
            }
          }
        }
      }
    };

    watch(
      () => [props.sprintId, props.teamId],
      () => {
        if (props.sprintId != null && props.teamId != null) {
          resetTable();
        }
      }
    );

    watch(isGradeScaleReady, (newValue) => {
      emit('grade-scale-ready-changed', newValue);
    });

    onMounted(async () => {
      console.log('Récupération des équipes...');
      await fetchAll().finally(() => {
        pageNotReady.value = false;
      });
    });

    return {
      columns,
      props,
      teamOptions,
      categories,
      calculateTotal,
      saveGrades,
      gradeScale,
      pageNotReady,
      user,
      isGradeScaleReady,
      BookOutline
    };
  },
});
</script>

<template>
  <Loading v-if="pageNotReady" />
  <div v-else>
    <Empty v-if="!isGradeScaleReady"
           title="Aucun barème"
           subtitle="Il n'y a aucune barème à afficher"
           :icon="BookOutline"
           :path="(user?.roles.includes('OL') || user?.roles.includes('PL')) ? '/preparation/gradeScale' : '/home'"
           :bouton-string="(user?.roles.includes('OL') || user?.roles.includes('PL'))? 'Créer les barèmes' : 'Retourner à la page d\'accueil'"
    />
    <div v-else>
      <n-card class="primary-container">
        <div v-if="props.teamId" class="scales-container">
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
        <div v-if="props.teamId" class="footer">
          <n-button type="primary" @click="saveGrades">Valider</n-button>
        </div>
      </n-card>
    </div>
  </div>
</template>

<style scoped>
.no-gradescale {
  text-align: center;
  margin-top: 250px;
}
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
  background-color: #ededed;
  padding: 10px;
}
.total {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 15px;
  text-align: center;
  font-weight: bold;
  background-color: #ededed;
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
.select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
.team-select {
  max-width: 300px;
}
</style>
