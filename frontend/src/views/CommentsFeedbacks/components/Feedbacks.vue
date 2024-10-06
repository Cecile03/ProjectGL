<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { feedbackService } from "@/services/feedback.service";
import type { Feedback } from "@/services/types";

type Props = {
  name: string;
  id_emitter?: number;
  id_team: number;
  id_sprint: number;
  name_team: string;
};

const props = defineProps<Props>();
const isFeedbackSent = ref(false);
const isFeedbackEdited = ref(false);
const content = ref('');
const feedbackId = ref<number | undefined>(undefined);
const feedbackDate = ref(new Date());
const result = ref(false);

const createOrUpdateFeedback = async () => {
  const feedback: Feedback = {
    id: isFeedbackEdited.value ? feedbackId.value : undefined,
    title: props.name,
    content: content.value,
    team: { id: props.id_team, name: props.name_team },
    sprint: { id: props.id_sprint },
    emitter: { id: props.id_emitter },
  };

  try {
    const feedbackCreated = await feedbackService.createFeedbacks(feedback);
    if (feedbackCreated) {
      const feedbacks = await feedbackService.getFeedbacksByTeamAndSprint(props.id_team, props.id_sprint);
      const createdFeedback = feedbacks?.find(f => f.title === feedback.title && f.content === feedback.content && f.emitter.id === feedback.emitter.id);

      if (createdFeedback) {
        feedbackId.value = createdFeedback.id;
        feedbackDate.value = createdFeedback.date ? new Date(createdFeedback.date) : new Date();
      }

      result.value = true;
      isFeedbackSent.value = true;
      isFeedbackEdited.value = false;
    }
  } catch (error) {
    console.error("Error creating or updating feedback.", error);
  }
};

const deleteFeedback = async () => {
  if (!feedbackId.value) return;

  try {
    const feedbackDeleted = await feedbackService.deleteFeedback(
      {
        id: feedbackId.value,
        title: props.name,
        content: content.value,
        team: { id: props.id_team, name: props.name_team },
        sprint: { id: props.id_sprint },
        emitter: { id: props.id_emitter },
      } as Feedback);
    if (feedbackDeleted) {
      resetFeedbackState();
    }
  } catch (error) {
    console.error("Error deleting feedback.", error);
  }
};

const editFeedback = () => {
  isFeedbackSent.value = false;
  isFeedbackEdited.value = true;
};

const resetFeedbackState = () => {
  isFeedbackSent.value = false;
  isFeedbackEdited.value = false;
  content.value = '';
  feedbackId.value = undefined;
  feedbackDate.value = new Date();
};

const fetchFeedbacks = async () => {
  try {
    const feedbacks = await feedbackService.getFeedbacksByTeamAndSprint(props.id_team, props.id_sprint);
    const specificFeedbacks = feedbacks?.filter(f => f.title === props.name && f.emitter.id === props.id_emitter);

    if (specificFeedbacks && specificFeedbacks.length > 0) {
      const lastFeedback = specificFeedbacks[specificFeedbacks.length - 1];
      content.value = lastFeedback.content;
      feedbackId.value = lastFeedback.id;
      feedbackDate.value = lastFeedback.date ? new Date(lastFeedback.date) : new Date();
      isFeedbackSent.value = true;
    }
  } catch (error) {
    console.error("Error fetching feedback.", error);
  }
};

onMounted(fetchFeedbacks);

watch(() => [props.id_team, props.id_sprint, props.id_emitter], async () => {
  resetFeedbackState();
  await fetchFeedbacks();
});
</script>

<template>
  <div>
    <div class="feedback-section">
      <h2>Retours</h2>
      <n-input
        v-model:value="content"
        class="feedback_content"
        type="textarea"
        placeholder="Écrivez votre retour ici"
        :autosize="{ minRows: 3 }"
        :disabled="isFeedbackSent"
      >
        <template #suffix>
          <n-button @click="createOrUpdateFeedback" :disabled="isFeedbackSent">Envoyer</n-button>
        </template>
      </n-input>

      <n-button v-if="result || isFeedbackSent" type="info" class="deleteFeedback" @click="deleteFeedback">Supprimer</n-button>
      <n-button v-if="isFeedbackSent" type="info" class="editFeedback" @click="editFeedback">Modifier</n-button>
    </div>
  </div>
</template>

<style scoped>
.feedback-section {
  margin: 20px;
}

.feedback-section h2 {
  margin-bottom: 10px;
}

.deleteFeedback, .editFeedback {
  margin-top: 10px;
  margin-right: 10px;
}
</style>
