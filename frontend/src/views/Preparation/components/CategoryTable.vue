<template>
  <div class="category">
    <div class="categoryHeader">
      <h3>
        <i v-if="category.name === 'Aide à l\'évaluation'" class="fa-solid fa-scale-balanced"></i>
        <i v-if="category.name === 'Présentation'" class="fa-solid fa-person-chalkboard"></i>
        <i v-if="category.name === 'Conformité du sprint'" class="fa-solid fa-clipboard-check"></i>
        <i v-if="category.name === 'Solution Technique'" class="fa-solid fa-tools"></i>
        <i v-if="category.name === 'Management du projet'" class="fa-solid fa-people-arrows"></i>
        {{ category.name }}
      </h3>
      <div v-if="category.isEditing">
        <n-button type="info" class="addRemove" text @click="addDetail">
          Ajouter un détail
        </n-button>
      </div>
    </div>
    <n-table :single-line="false">
      <thead>
      <tr>
        <th class="{{category.isEditing ? 'criteria-delete' : 'criteria'}}">Critère</th>
        <th class="mark" >Note barème</th>
        <th class="delete" v-if="category.isEditing"></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(detail, index) in category.details" :key="index">
        <td>
          <template v-if="detail.isEditing">
            <n-input :value="detail.name" @update:value="updateName(index, $event)"/>
          </template>
          <template v-else>
            {{ detail.name }}
          </template>
        </td>
        <td>
          <template v-if="detail.isEditing">
            <n-input-number v-model:value="detail.mark" :min="0" :max="20"/>
          </template>
          <template v-else>
            /{{ detail.mark }}
          </template>
        </td>
        <td v-if="category.isEditing">
          <n-button type="error" class="addRemove" text @click="removeDetail(index)">
            Retirer
          </n-button>
        </td>
      </tr>
      </tbody>
    </n-table>
  </div>
</template>

<script lang="ts">
import { defineComponent} from 'vue';

interface Detail {
  name: string;
  mark: number;
  isEditing?: boolean;
}

interface Category {
  name: string;
  details: Detail[];
  isEditing?: boolean;
}

export default defineComponent({
  props: {
    category: {
      type: Object as () => Category,
      required: true
    }
  },
  setup(props) {
    const updateName = (index: number, newName: string) => {
      props.category.details[index].name = newName;
    };

    const addDetail = () => {
      props.category.details.push({
        name: `Detail ${props.category.details.length + 1}`,
        mark: 0,
        isEditing: props.category.isEditing,
      });
    };

    const removeDetail = (index: number) => {
      props.category.details.splice(index, 1);
    };

    return {
      updateName,
      addDetail,
      removeDetail
    };
  }
});
</script>

<style scoped>
.criteria {
  width: 70%;
}
.criteria-delete{
  width: 60%;
}
.mark {
  width: 30%;
}
.delete {
  width: 10%;
}
.categoryHeader {
  display: flex;
  gap: 10px;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}
.addRemove{
  padding: 5px;
}
</style>
