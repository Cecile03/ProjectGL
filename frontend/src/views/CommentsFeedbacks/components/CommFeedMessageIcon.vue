<template>
  <div>
    <div class="tri-selector">
      <h4>Trier par émetteur:</h4>
      <n-select id="tri" v-model:value="selectedRole" :options="[{label: 'Tous', value: 'Tous'}, {label: 'Coach Technique', value: 'TC'}, {label: 'Professeur référent', value: 'SS'}]" placeholder="Trier par rôle" />
    </div>
    <n-collapse>
      <n-collapse-item title="Retours">
        <div v-if="sortedFeedbacks.length === 0" class="empty-text">
          <h4>Il n'y a pas de retours pour l'instant.</h4>
        </div>
        <n-card v-else v-for="feedback in sortedFeedbacks" :key="feedback.id" size="small" class="n-card">
          <div class="header">
            <h4>{{ feedback.emitter.firstName }} {{ feedback.emitter.lastName }}</h4>
            <div class="roles-tag">
              <n-tag v-for="(role, index) in feedback.emitter.roles" :key="index" :class="`role-${role}`">{{ role }}</n-tag>
            </div>
          </div>
          <div class="content">
            <div>{{ feedback.content }}</div>
            <div>{{ feedback.date }}</div>
          </div>
        </n-card>
      </n-collapse-item>
      <n-collapse-item title="Commentaires" v-if="!isStudent">
        <div v-if="sortedComments.length === 0" class="empty-text">
          <h4>Il n'y a pas de commentaires pour l'instant.</h4>
        </div>
        <n-card v-else v-for="comment in sortedComments" :key="comment.id" size="small" class="n-card">
          <div class="header">
            <h4>{{ comment.emitter.firstName }} {{ comment.emitter.lastName }}</h4>
            <div class="roles-tag">
              <n-tag v-for="(role, index) in comment.emitter.roles" :key="index" :class="`role-${role}`">{{ role }}</n-tag>
            </div>
          </div>
          <div class="content">
            <div>{{ comment.content }}</div>
            <div>{{ comment.date }}</div>
          </div>
        </n-card>
      </n-collapse-item>
    </n-collapse>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { feedbackService } from "@/services/feedback.service";
import { commentService } from "@/services/comment.service";
import type { Feedback, Comment } from "@/services/types";
import {useUserStore} from '@/stores/useUserStore';

type Props = {
  id_team: number;
  id_sprint: number;
};

const user = useUserStore().getUser();
const isStudent = user?.roles.includes('OS');
const selectedRole = ref('Tous');

enum UserRole {
  TC = 'TC',
  SS = 'SS',
  OS = 'OS',
  PL = 'PL',
  OL = 'OL'
}

function stringToUserRole(role: string): UserRole | undefined {
  switch (role) {
    case 'TC':
      return UserRole.TC;
    case 'SS':
      return UserRole.SS;
    default:
      return undefined;
  }
}

const sortedFeedbacks = computed(() => {
  if (selectedRole.value === 'Tous') {
    return feedbacks.value;
  }
  if (selectedRole.value !== 'TC' && selectedRole.value !== 'SS') {
    console.error("Erreur lors du triage, rôle invalide");
    return feedbacks.value;
  }
  //@ts-ignore
  return feedbacks.value.filter(feedback => feedback.emitter?.roles?.includes(stringToUserRole(selectedRole.value)));
});

const sortedComments = computed(() => {
  if (selectedRole.value === 'Tous') {
    return comments.value;
  }
  //@ts-ignore
  return comments.value.filter(comment => comment.emitter?.roles?.includes(stringToUserRole(selectedRole.value)));
});

const props = defineProps<Props>();
const show = ref(false);
const feedbacks = ref<Feedback[]>([]);
const comments = ref<Comment[]>([]);

const fetchFeedbacksAndComments = async () => {
  if(props.id_sprint === 0) return;
  try {
      feedbacks.value = await feedbackService.getFeedbacksByTeamAndSprint(props.id_team, props.id_sprint) || [];
    if(!isStudent){
      comments.value = await commentService.getCommentsByTeamAndSprint(props.id_team, props.id_sprint) || [];
    }
  } catch (error) {
    console.error("Erreur lors de la récupération des commentaires et feedbacks", error);
  }
};

watch(() => show.value, (newVal) => {
  if (newVal) {
    fetchFeedbacksAndComments();
  }
});

onMounted(() => {
  fetchFeedbacksAndComments();
});

watch(() => props.id_sprint, fetchFeedbacksAndComments, { immediate: true });
watch(() => props.id_team, fetchFeedbacksAndComments, { immediate: true });
</script>

<style scoped>
.tri-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

#tri {
  width: 200px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-text {
  text-align: center;
}

.roles-tag {
  display: flex;
  gap: 2px;
}

.n-card {
  margin-bottom: 3px;
}

.role-TC {
  background-color: #87a61e;
  margin-right: 5px;
}

.role-SS {
  background-color: #a11ea3;
  margin-right: 5px;
}

.role-OL {
  background-color: #0056b3;
  margin-right: 5px;
}

.role-PL {
  background-color: #ee4836;
  margin-right: 5px;
}

.role-OS {
  background-color: #e3b13c;
  margin-right: 5px;
}

.content {
  margin-top: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>