<template>
  <n-tooltip placement="right-end" trigger="hover">
    <template #trigger>
      <i class="fa-solid fa-info-circle"></i>
    </template>
    <div class="information-tooltip">
      <h3>{{ gradeScale?.name }}</h3>
      <div v-for="(category, index) in categories" :key="index">
        <p class="category-text">{{ category.name }}</p>
        <ul>
          <li v-for="(detail, index) in category.details" :key="index">
            {{ detail.name }} {{ detail.description }}
          </li>
        </ul>
      </div>
    </div>
  </n-tooltip>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import { NTooltip } from 'naive-ui';
import { gradeScaleService } from '@/services/gradeScale.service';
import { categoryService } from '@/services/category.service';
import { detailService } from '@/services/detail.service';

export default defineComponent({
  components: {
    NTooltip
  },
  setup() {

    type DetailType = {
      id: number;
      name: string;
      mark: number;
      description: string;
    };

    type CategoryType = {
      id: number;
      name: string;
      details: DetailType[];
    };

    type GradeScaleType = {
      id: number;
      name: string;
      description: string;
    };

    const gradeScale = ref<GradeScaleType | null>(null);
    const categories = ref<CategoryType[]>([]);

    onMounted(async () => {
      gradeScale.value = await gradeScaleService.getGradeScaleMarks();
      console.log(gradeScale.value)
      const categoriesData = await categoryService.getCategoriesByGradeScaleId(gradeScale?.value?.id ?? 0);

      for (const category of categoriesData) {
        const detailsData = await detailService.getDetailsByCategoryId(category.id);
        const details = detailsData.map((detail: any) => ({
          id: detail.id,
          name: detail.name,
          mark: detail.mark,
          description: '~'+detail.mark+'/20',
        }));

        categories.value.push({
          id: category.id,
          name: category.name,
          details: details,
        });
      }
    });

    return {
      gradeScale,
      categories,
    };
  },
});
</script>

<style scoped>
.information-tooltip {
  padding: 10px;
}
.fa-solid.fa-info-circle {
  font-size: 20px; /* Taille de police réduite pour les petits écrans */
  padding: 10px; /* Padding ajusté */
}
.category-text {
  text-decoration: underline;
}
</style>