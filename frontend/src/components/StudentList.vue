<script setup lang="ts">
import { h } from 'vue';
import { type User } from '../services/types';
import type { DataTableColumns } from 'naive-ui';
type Props = {
  students: User[];
};
defineProps<Props>();

type RowData = {
  key: number;
  firstName: string;
  lasstName: string;
  gender: string;
};

const createColumns = (): DataTableColumns<RowData> => [
  {
    title: 'Nom',
    key: 'lastName',
    sorter: 'default',
  },
  {
    title: 'Prénom',
    key: 'firstName',
    sorter: 'default',
  },
  {
    title: 'Sexe',
    key: 'gender',
    sorter: 'default',
    render: (row) => {
      const isFemale = row.gender === 'female';
      return h(
        'span',
        {
          class: ['sexe', { isFemale: isFemale }],
        },
        isFemale ? 'Femme' : 'Homme'
      );
    },
  },
];
</script>

<template>
  <n-data-table ref="table" :columns="createColumns()" :data="students" />
</template>

<style scoped>
.studentsTable {
}
.sexe {
  padding: 10px;
  border-radius: 5px;
  background-color: rgb(226, 239, 248);
}
.isFemale {
  background-color: rgb(248, 226, 230);
}
</style>
