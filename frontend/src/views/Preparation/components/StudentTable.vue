<script setup lang="ts">
import { h, ref, watch, onMounted } from 'vue';
import type { VNode } from 'vue';
import { NInput, NSelect, NButton, useDialog } from 'naive-ui';
import { toast } from 'vue3-toastify';
import { fetchData } from '@/services/convertCSVtoData.service';
import { convertToCSV, downloadCSV } from '@/services/convertDataToCSV.service';

// Variables
let editing = ref(false);
const data = ref<RowData[]>([]);
const dialog = useDialog();

//Types
type RowData = {
  key: number;
  id: string;
  name: string;
  gender: string;
  isValid: boolean;
};

type Column = {
  title: VNode | string;
  key: string;
  render?: (row: RowData, index: number) => VNode | string;
  sorter?: string;
  width?: string;
  align?: string;
};

// Définition des props
type Props = {
  file: File;
  onExportClick: () => void;
};
const props = defineProps<Props>();

const file = ref(props.file);

//Récupération du fichier CSV envoyé par l'utilisateurs
onMounted(() => {
  if (props.file) {
    console.log('Fichier reçu : ' + file.value.name);
  } else {
    console.error('Aucun fichier reçu');
  }
});

// Fonctions
// Fonction pour d'initialisation des colonnes du tableau de données
// (Initialisation des noms et de la taille des colonnes)
const getColumns = () => {
  let baseColumns: Column[] = [
    {
      title: 'id',
      key: 'id',
      width: '10%',
    },
    {
      title: 'Nom - Prénom',
      key: 'name',
      sorter: 'default',
      render(row: RowData, index: number) {
        return editing.value
          ? h(NInput, {
              value: row.name,
              onUpdateValue(v) {
                data.value[index].name = v;
              },
            })
          : row.name;
      },
    },
    {
      title: 'Genre',
      key: 'gender',
      width: '15%',
      render(row: RowData, index: number) {
        return editing.value
          ? h(NSelect, {
              value: row.gender,
              options: [
                { label: 'M', value: 'M' },
                { label: 'F', value: 'F' },
              ],
              onUpdateValue(v) {
                data.value[index].gender = v;
                data.value[index].isValid = !!v;
              },
            })
          : row.gender;
      },
    },
  ];

  // Ajout de la colonne 'Supprimer' si on est en mode édition
  if (editing.value) {
    baseColumns.push({
      title: h('i', { class: 'fa-solid fa-trash' }),
      key: 'delete',
      width: '10%',
      align: 'center',
      render(row: RowData) {
        return h(
          NButton,
          {
            style: { backgroundColor: '#e73535', color: '#fff' },
            strong: true,
            tertiary: true,
            size: 'small',
            onClick: () => alertDelete(row).handleConfirm(),
          },
          {
            default: () => '-',
          }
        );
      },
    });
  }
  return baseColumns;
};

let columns = ref(getColumns());

// Fonction pour récupérer les données du fichier CSV et les afficher dans le tableau
const fetchDataFromCSV = async () => {
  try {
    const tableData = await fetchData(file.value);

    // Mise à jour de 'data' avec les valeurs de 'tableData'
    data.value = tableData.map((row: any[], index: number) => ({
      key: index,
      id: row[0],
      name: row[1],
      gender: row[2],
      isValid: true,
    }));
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

//Permet d'ajouter une ligne vierge au tableau
const addRow = () => {
  const newRow: RowData = {
    key: data.value.length,
    id: `${data.value.length + 1}`,
    name: 'Nom - Prénom',
    gender: 'Genre',
    isValid: false,
  };
  data.value.push(newRow);
};

//Permet de supprimer une ligne du tableau
const deleteRow = (row: RowData) => {
  const index = data.value.findIndex((r) => r.key === row.key);
  data.value.splice(index, 1);

  // Actualise les ID
  for (let i = 0; i < data.value.length; i++) {
    data.value[i].id = `${i + 1}`;
  }
};

// Gère les notifications en mode édition
const notify = () => {
  //Préviens l'utilisateur qu'il est en mode édition ou qu'il en sort
  if (editing.value) {
    // Vérifiez si toutes les lignes sont valides avant de sauvegarder
    if (data.value.every((row) => row.isValid)) {
      toast.success('Vous passez en mode édition', {
        autoClose: 500,
      });
    } else {
      toast.error('Veuillez sélectionner un genre pour toutes les lignes', {
        autoClose: 700,
      });
    }
  } else {
    toast.success('Vos modifications ont été enregistré', {
      autoClose: 500,
    });
  }
};

// S'assure que l'utilisateur veut bien supprimer la ligne
const alertDelete = (row: RowData) => {
  return {
    handleConfirm() {
      dialog.warning({
        title: 'Confirmation de suppression',
        content: 'Êtes-vous certain de vouloir supprimer cet élève ?',
        positiveText: 'Oui',
        negativeText: 'Non',
        onPositiveClick: () => {
          deleteRow(row);
        },
        onNegativeClick: () => {
          console.log('Action annulée');
        },
      });
    },
  };
};
// Fonction de téléchargement du fichier CSV
const exportTable = () => {
  const csv = convertToCSV(data.value);
  downloadCSV(csv, 'ListeEleveLD.csv');
  props.onExportClick();
};

// Actualise les colonnes du tableau en fonction du mode d'édition
watch(editing, () => {
  columns.value = getColumns();
});

// Récupère les données du fichier CSV
fetchDataFromCSV();
</script>

<template>
  <div class="main_container">
    <div class="table-container">
      <div class="top-container">
        <h2>Fichier : {{ file.name }}</h2>
        <i
          :class="[
            'icon',
            editing
              ? 'fa-solid fa-pen-to-square'
              : 'fa-regular fa-pen-to-square',
          ]"
          @click="
            () => {
              if (data.every((row: RowData) => row.isValid)) {
                //Enregistrement des notifications
                editing = !editing;
                notify();
              } else {
                //Envoie une notification erreur
                notify();
              }
            }
          " />
      </div>
      <n-data-table
        ref="table"
        :single-line="false"
        :columns="columns"
        :data="data"
        :max-height="'50vh'" />
      <n-button type="info" class="add" v-if="editing" @click="addRow">
          <i class="fa-solid fa-plus"></i>
      </n-button>
    </div>
    <div class="btn-container">
      <n-button @click="exportTable">Exporter</n-button>
      <n-button type="info">Annuler</n-button>
    </div>
  </div>
  
</template>

<style scoped>
.top-container {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}
.main_container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.table-container {
  width: 40%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 5px;
}
.btn-container {
  bottom: 0; 
  width: 50%;
  display: flex; 
  justify-content: center; 
  padding-top: 10px;
}

.add {
  align-self: flex-start;
}
.icon {
  font-size: 30px;
  margin-left: 15px;
}
</style>
