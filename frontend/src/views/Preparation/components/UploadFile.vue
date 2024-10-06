<script setup lang="ts">
import { ref } from 'vue';
import StudentTable from './StudentTable.vue';
import { useMessage } from 'naive-ui';

const emit = defineEmits(['validate']);
const message = useMessage();
const isUploadMode = ref(true);
const uploadedFile = ref<File | null>(null);
const isFileUploaded = ref(false);
const fileName = ref('');
const isFormatValid = ref(true);

const handleFileUpload = (file: File) => {
  if (!file.name.endsWith('.csv')) {
    message.error('Seuls les fichiers CSV sont acceptés');
    isFormatValid.value = false;
    isFileUploaded.value = true;
    return;
  }
  isFormatValid.value = true;
  uploadedFile.value = file;
  isFileUploaded.value = true;
  fileName.value = file.name;
};
const handleInputChange = (event: Event) => {
  const fileInput = event.target as HTMLInputElement;
  const file = (event.target as HTMLInputElement).files?.[0];
  if (file) {
    handleFileUpload(file);
  }
  fileInput.value = '';
};
const handleDragOver = (event: DragEvent) => {
  event.preventDefault();
};
const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const file = event.dataTransfer?.files[0];
  if (file) {
    if (file.name.endsWith('.csv')) {
      handleFileUpload(file);
    } else {
      message.error('Seuls les fichiers CSV sont acceptés');
    }
  }
};
const validate = () => {
  if (isFileUploaded.value) {
    isUploadMode.value = false;
  } else {
    message.warning('Veuillez télécharger un fichier');
  }
};
const cancel = () => {
  uploadedFile.value = null;
  isFileUploaded.value = false;
  fileName.value = '';
};

const nextStep = () => {
  emit('validate');
};
</script>

<template>
  <n-card class="drag-drop-container" v-if="isUploadMode">
    <h1 class="title">Joindre un Fichier</h1>
    <div class="dropzone" @dragover="handleDragOver" @drop="handleDrop">
      <div class="before-content" v-if="!isFileUploaded && !uploadedFile">
        <i
          style="margin-right: 2%; font-size: 50px"
          class="fa-solid fa-file-import"></i>
        Glissez et déposez un fichier ici ou cliquez pour sélectionner un
        fichier
      </div>
      <div class="after-invalid-upload" v-if="!isFormatValid && isFileUploaded">
        <i
            style="margin-right: 2%; font-size: 50px"
            class="fa-solid fa-file-circle-exclamation"></i>
        Sélectionnez un fichier au format valide
      </div>
      <div class="after-content" v-if="isFileUploaded && uploadedFile && isFormatValid">
        <i
          style="margin-right: 2%; font-size: 50px"
          class="fa-solid fa-file-csv"></i>
        {{ fileName }}
      </div>
      <input
        type="file"
        accept=".csv"
        @change="handleInputChange"
        class="file-input" />
    </div>
    <p class="sub-upload-txt">Formats pris en compte : CSV</p>
    <template #footer>
      <div class="button-container">
        <n-button @click="validate" :disabled="!isFormatValid">Valider</n-button>
        <n-button type="info" @click="cancel">Annuler</n-button>
      </div>
    </template>
  </n-card>
  <StudentTable
    v-if="!isUploadMode && uploadedFile"
    :file="uploadedFile"
    :on-export-click="nextStep" />
</template>

<style scoped>
.dropzone {
  position: relative;
  width: 80%;
  height: 200px;
  border: 2px dashed #aaa;
  display: flex;
  justify-content: center;
  border-radius: 10px;
  margin: auto;
}
/* Responsive adaptation depending on screen size */
@media (min-width: 600px) and (max-width: 900px) {
  .dropzone {
    width: 80%;
  }
}
@media (max-width: 600px) {
  .dropzone {
    width: 100%;
  }
}
.before-content {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  width: 100%;
}
.after-invalid-upload {
  display: flex;
  margin: 0 auto;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  width: 100%;
}
.after-content {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  width: 100%;
}
.drag-drop-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 2%;
}
.file-input {
  opacity: 0;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}
.sub-upload-txt {
  margin-top: 1%;
  margin-right: 10%;
  font-size: 12px;
  color: #6c757d;
  text-align: right;
}
.title {
  font-size: 24px;
  margin-bottom: 1%;
  margin-left: 10%;
  text-align: left;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
