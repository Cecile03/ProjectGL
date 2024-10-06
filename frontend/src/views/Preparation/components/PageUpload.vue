<template>
  <div v-if="isStudentsExist" class="studentsExist">
    <h2>Des étudiants ont déjà été importés :</h2>
    <div class="table-container">
      <StudentList :students="students" />
    </div>
    <n-popconfirm
      :negative-text="null"
      :positive-text="'Confirmer'"
      @positive-click="handleDeleteStudents">
      <template #trigger>
        <n-button
          class="deleteBtn"
          type="error"
          :loading="deleteStudentsLoading">
          Supprimer les étudiants
        </n-button>
      </template>
      En supprimant les étudiants ci-dessus, toutes les équipes associées seront
      également supprimées.
    </n-popconfirm>
  </div>
  <div v-else class="uploader-container">
    <UploadGradeSpreadsheet
      @fileUploaded="handleFileUpload"
      @cancel="clearStudents" />
    <n-scrollbar style="max-height: 400px">
      <div class="content-scrollbar">
        <n-data-table
          :columns="columns"
          :data="students"
          class="table"></n-data-table>
      </div>
    </n-scrollbar>
    <n-button
      @click="transferStudents"
      :loading="createStudentsLoading"
      :disabled="!isFileValidated"
      class="main-button"
      >Ajouter ces étudiants</n-button
    >
  </div>
</template>

<script>
import { AxiosClient } from '../../../services/index.ts';
import { h, ref, toRaw } from 'vue';
import UploadGradeSpreadsheet from '@/views/Preparation/components/UploadGradeSpreadsheet.vue';
import { NInputNumber, useMessage } from 'naive-ui';
import { usePreparationStore } from '../../../stores/usePreparationStore.ts';
import StudentList from '@/components/StudentList.vue';
import { userService } from '@/services/user.service';
import { toast } from 'vue3-toastify';

const errorMessage = ref('');
const students = ref([]);
const isStudentsExist = ref(false);

export default {
  components: { UploadGradeSpreadsheet, StudentList },
  mounted: () => {},
  setup(props, context) {
    const message = useMessage();
    const preparationStore = usePreparationStore();

    const studentList = toRaw(preparationStore.getStudents());
    if (studentList.length) {
      students.value = studentList;
      console.log(studentList);
      isStudentsExist.value = true;
    }

    const deleteStudentsLoading = ref(false);
    const handleDeleteStudents = () => {
      deleteStudentsLoading.value = true;
      userService
        .deleteStudents()
        .then(() => {
          toast.success('Etudiants supprimés');
          students.value = [];
          isStudentsExist.value = false;
          window.location.reload();
        })
        .finally(() => (deleteStudentsLoading.value = false));
    };

    const isFileValidated = ref(false);
    const clearStudents = () => {
      students.value = [];
    };

    const updateFileUploaded = (value) => {
      fileUploaded.value = value;
    };

    const updateAverage = (student) => {
      if (student.bachelor !== 'Oui') {
        let average =
          (student.padl * coef.value.padl +
            student.pdlo * coef.value.pdlo +
            student.pwnd * coef.value.pwnd +
            student.irs * coef.value.irs +
            student.stages7 * coef.value.stages7 +
            student.s5 * coef.value.s5 +
            student.s6 * coef.value.s6) /
          (coef.value.padl +
            coef.value.pdlo +
            coef.value.pwnd +
            coef.value.irs +
            coef.value.stages7 +
            coef.value.s5 +
            coef.value.s6);
        student.average = parseFloat(average.toFixed(2));
      }
    };

    const createInputNumberTitle = (title, key) => {
      return h(NInputNumber, {
        showButton: false,
        value: coef.value[key],
        max: 9,
        min: 0,
        placeholder: '',
        align: 'center',
        onUpdateValue: (value) => {
          coef.value[key] = value;
          students.value.forEach((student) => updateAverage(student));
        },
      });
    };

    const handleFileUpload = (file) => {
      const fileExtension = file.name.split('.').pop();
      if (fileExtension !== 'csv') {
        errorMessage.value = 'Veuillez sélectionner un fichier au format .csv';
        return;
      }
      const formData = new FormData();
      formData.append('file', file);

      AxiosClient.post('/upload', formData, {})
        .then((response) => {
          console.log('Réponse du serveur:', response.data);
          students.value = response.data;
          console.log('Contenu de students:', students.value);
          console.log(JSON.stringify(students.value, null, 2));

          for (const element of students.value) {
            console.log(element.bachelor);
            if (element.bachelor === 'B') {
              console.log(element.bachelor);
              element.bachelor = 'Oui';
            } else {
              element.bachelor = 'Non';
            }
          }
          isFileValidated.value = true;
        })
        .catch((error) => {
          // Gérer les erreurs
          console.error("Erreur lors de l'envoi du fichier:", error);
        });
    };
    const isBachelor = (student) => {
      return student.bachelor === 'Oui';
    };

    const createStudentsLoading = ref(false);
    const transferStudents = () => {
      const list = [];
      for (const element of students.value) {
        const firstName = element.fullName.split(' ')[1];
        const lastName =
          element.fullName.split(' ')[0].charAt(0) +
          element.fullName.split(' ')[0].slice(1).toLowerCase();
        const dataToWrite = {
          firstName: firstName,
          lastName: lastName,
          role: 'ROLE_USER',
          email:
            firstName.toLowerCase() +
            '.' +
            lastName.toLowerCase() +
            '@reseau.eseo.fr',
          option: 'LD',
          gender: element.gender === 'M' ? 'male' : 'female',
          gradePast: element.average,
          isBachelor: isBachelor(element),
        };
        list.push(dataToWrite); // Add dataToWrite to the list
      }

      createStudentsLoading.value = true;
      userService
        .createStudentList(list)
        .then(() => {
          message.success('Les étudiants ont été ajoutés avec succès.');
          const users = [];
          for (const item of list) {
            const user = {
              firstName: item.firstName,
              lastName: item.lastName,
              email: item.email,
              gender: item.gender,
              option: item.option,
              roles: item.role,
              isBachelor: item.isBachelor,
              gradePast: item.gradePast,
            };
            users.push(user);
          }
          preparationStore.addStudentList(users);
          context.emit('validate');
        })
        .catch(() =>
          message.error(
            'Une erreur est survenue lors de la création des étudiants.'
          )
        )
        .finally(() => (createStudentsLoading.value = false));
    };

    const keysAndTitles = [
      { key: 'padl', title: 'PADL' },
      { key: 'pdlo', title: 'PDLO' },
      { key: 'pwnd', title: 'PWND' },
      { key: 'irs', title: 'IRS' },
      { key: 'stages7', title: 'Stage S7' },
      { key: 's5', title: 'S5' },
      { key: 's6', title: 'S6' },
    ];

    const coef = ref({
      padl: 1,
      pdlo: 1,
      pwnd: 1,
      irs: 1,
      stages7: 1,
      s5: 1,
      s6: 1,
    });
    const fileUploaded = ref(false);
    const columns = [
      {
        title: 'Coefficients :',
        key: 'coef',
        children: [
          { title: 'ID', key: 'id', align: 'center', width: '5%' },
          {
            title: 'Nom complet',
            key: 'fullName',
            align: 'center',
            width: '20%',
          },
          { title: 'Genre', key: 'gender', align: 'center', width: '7.5%' },
          {
            title: 'Bachelor',
            key: 'bachelor',
            align: 'center',
            width: '9.5%',
          },
          { title: 'Moyenne', key: 'average', align: 'center', width: '9.5%' },
        ],
      },
      ...keysAndTitles.map((item, index) => {
        return {
          key: `coeff${index + 1}`,
          title: createInputNumberTitle(item.title, item.key),
          children: [
            {
              title: item.title,
              key: item.key,
              align: 'center',
              render: (row) => {
                return row.bachelor === 'Oui' ? '' : row[item.key];
              },
            },
          ],
        };
      }),
    ];

    return {
      isStudentsExist,
      handleDeleteStudents,
      deleteStudentsLoading,
      students,
      createStudentsLoading,
      columns,
      handleFileUpload, // Return the method, so it can be used in the template
      updateFileUploaded, // Return the method, so it can be used in the template
      coef,
      updateAverage,
      clearStudents,
      isFileValidated,
      transferStudents,
    };
  },
};
</script>

<style scoped>
.studentsExist {
  text-align: center;
  width: 100%;
}
.studentsExist h2 {
  margin-bottom: 10px;
}
.studentsExist .table-container {
  max-width: 500px;
  min-width: 300px;
  width: 100%;
  height: 65vh;
  box-shadow: 0 0 10px 0 #c9c9c9a3;
  overflow: scroll;
  margin: 0 auto;
}
.studentsExist .deleteBtn {
  margin-top: 20px;
}
.uploader-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 80%;
  margin: auto;
}
.table {
  margin: 0 auto;
  width: 100%;
  font-size: 1vw;
}
.table th {
  white-space: nowrap;
}
.main-button {
  margin-top: 20px;
}
.n-data-table thead {
  position: sticky;
  top: 0;
  z-index: 1;
  background-color: white;
}
</style>
