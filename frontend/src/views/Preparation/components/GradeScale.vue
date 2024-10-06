<template>
  <Loading v-if="pageNotReady" />
  <div v-else class="gradeScale">
    <h1 class="title">Barèmes d'évaluation</h1>
    <div v-for="(gradeScale, gradeScaleIndex) in gradeScales" :key="gradeScaleIndex" class="gradeScaleRow">
      <div class="gradeScaleHeader">
        <h2>{{ gradeScale.name }}</h2>
      </div>
      <div class="categories">
        <CategoryTable v-for="(category, categoryIndex) in gradeScale.categories" :key="categoryIndex" :category="category" :is-editing-all="isEditingAll" class="category"/>
      </div>
    </div>
    <div class="button">
      <NButton class="warningButton" type="error" @click="fetchExampleData">Réinitialiser tout</NButton>
      <NButton class="secondaryButton" @click="toggleAllEditing">{{ isEditingAll ? 'Finaliser la modification' : 'Mode édition' }}</NButton>
      <NButton class="primaryButton" v-if="!isEditingAll" @click="saveToDatabase">Valider</NButton>
      <NButton class="secondaryButton" @click="handleValidation">Retour au menu principal</NButton>
    </div>
  </div>
</template>



<script setup lang="ts">
import { NButton, NInput } from 'naive-ui';
import { onMounted, ref } from 'vue';
import { gradeScaleService } from "@/services/gradeScale.service";
import { categoryService } from "@/services/category.service";
import { detailService } from "@/services/detail.service";
import { toast } from 'vue3-toastify';
import router from '@/router';
import Loading from '@/components/Loading.vue';
import CategoryTable from '@/views/Preparation/components/CategoryTable.vue';

interface Detail {
name: string;
mark: number | null;
isEditing?: boolean;
}

interface Category {
  name: string;
  details: Detail[];
  isEditing?: boolean;
}

interface GradeScale {
  name: string;
  categories: Category[];
  isEditing?: boolean;
}

const pageNotReady = ref(true);

const handleValidation = async () => {
  await router.push('/home');
};

const exampleGradeScale: GradeScale = {
  name: 'Echelle de notes',
  categories: [
    {
      name: 'Aide à l\'évaluation',
      details: [
        {
          name: 'Excellent travail',
          mark: 18,
        },
        {
          name: 'Super travail',
          mark: 16,
        },
        {
          name: 'Bon travail',
          mark: 14,
        },
        {
          name: 'Travail suffisant',
          mark: 12,
        },
        {
          name: 'A retravailler',
          mark: 10,
        },
        {
          name: 'Travail fragile',
          mark: 8,
        },
        {
          name: 'Mauvais travail',
          mark: 6,
        },
      ],
    },
  ],
};
const exampleGradeScaleProject: GradeScale = {
  name: 'Barème de notation du projet',
  categories: [
    {
      name: 'Conformité du sprint',
      details: [
        {
          name: 'Respect des délais',
          mark: 5,
        },
        {
          name: 'Flexibilité face aux changements',
          mark: 5,
        },
      ],
    },
    {
      name: 'Solution Technique',
      details: [
        {
          name: 'Choix des technologies',
          mark: 4,
        },
        {
          name: 'Sécurité des données',
          mark: 4,
        },
      ],
    },
    {
      name: 'Management du projet',
      details: [
        {
          name: 'Communication d\'équipe',
          mark: 3,
        },
        {
          name: 'Résolution des conflits',
          mark: 5,
        },
      ],
    },
  ],
}

const exampleGradeScalePresentation: GradeScale = {
  name: 'Barème de notation de la présentation',
  categories: [
    {
      name: 'Présentation',
      details: [
        {
          name: 'Qualité du design',
          mark: 2,
        },
        {
          name: 'Originalité du design',
          mark: 2,
        },
      ],
    },
  ]
}

const gradeScales = ref<GradeScale[]>([]);


const saveToDatabase = async () => {
  try {
    if(await gradeScaleService.createGradeScale(gradeScales.value)) {
      toast.success('Les barèmes ont été créés avec succès');
    } else {
      toast.error('Une erreur est survenue lors de la création des barèmes');
    }
  }catch (error) {
    console.error(error);
    toast.error('Une erreur est survenue lors de la création des barèmes');
  }
};

const isEditingAll = ref(false);

const toggleAllEditing = () => {
  isEditingAll.value = !isEditingAll.value;
  gradeScales.value.forEach(gradeScale => {
    gradeScale.isEditing = isEditingAll.value;
    gradeScale.categories.forEach(category => {
      category.isEditing = isEditingAll.value;
      category.details.forEach(detail => {
        detail.isEditing = isEditingAll.value;
      });
    });
  });
};

const loadData = async () => {
  const gradeScalesData = await gradeScaleService.getAllGradeScales();
  if (gradeScalesData && gradeScalesData.length === 3) {
    for (const gradeScale of gradeScalesData) {
      const categoriesData = await categoryService.getCategoriesByGradeScaleId(gradeScale.id);
      if (categoriesData) {
        gradeScale.categories = categoriesData;
        for (const category of gradeScale.categories) {
          const detailsData = await detailService.getDetailsByCategoryId(category.id);
          if (detailsData) {
            category.details = detailsData;
          }
        }
      }
    }
    gradeScales.value = gradeScalesData;
  } else {
    fetchExampleData()
  }
};

const fetchExampleData = () => {
  gradeScales.value = [exampleGradeScale, exampleGradeScaleProject, exampleGradeScalePresentation];
};

onMounted(async () => {
  await loadData().finally(() => (pageNotReady.value = false));
});

</script>

<style scoped>
.gradeScale {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100vw;
  padding: 40px;

}

.title {
  font-size: 2em;
  color: #333;
  margin-bottom: 20px;
}

.gradeScaleRow {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 80%;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
  background-color: #fff;
}

.gradeScaleHeader {
  display: flex;
  gap: 10px;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 0;
  border-bottom: 1px solid #ddd;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  gap: 0 20px;
}

.category {
  flex: 1 1 calc(50% - 10px);
  margin-bottom: 10px;
}

.category:last-child {
  margin-right: 0;
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

.warningButton {
  margin: 20px 0;
  color : var(--redColor);
  background-color: white;
  padding: 10px 20px;
  border-radius: 5px;
  border: 2px solid;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.secondaryButton:hover {
  background-color: #0056b3;
}

.warningButton:hover {
  background-color: var(--redColor);
}


</style>


