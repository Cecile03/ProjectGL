<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { commentService } from '@/services/comment.service';
import type { Comment } from "@/services/types";

type Props = {
  name: string;
  id_emitter?: number;
  id_team: number;
  id_sprint: number;
  name_team: string;
  placeholder: string;
};

const props = defineProps<Props>();
const isCommentSent = ref(false);
const isCommentEdited = ref(false);
const content = ref('');
const commentId = ref<number | undefined>(undefined);
const commentDate = ref(new Date());
const result = ref(false);

const createOrUpdateComment = async () => {
  const comment: Comment = {
    id: isCommentEdited.value ? commentId.value : undefined,
    title: props.name,
    content: content.value,
    team: { id: props.id_team, name: props.name_team },
    sprint: { id: props.id_sprint },
    emitter: { id: props.id_emitter },
  };

  try {
    const commentCreated = await commentService.createComments(comment);
    if (commentCreated) {
      const comments = await commentService.getCommentsByTeamAndSprint(props.id_team, props.id_sprint);
      const createdComment = comments?.find(c => c.title === comment.title && c.content === comment.content && c.emitter.id === comment.emitter.id);

      if (createdComment) {
        commentId.value = createdComment.id;
        commentDate.value = createdComment.date ? new Date(createdComment.date) : new Date();
      }

      result.value = true;
      isCommentSent.value = true;
      isCommentEdited.value = false;
    }
  } catch (error) {
    console.error("Error creating or updating comments.", error);
  }
};

const deleteComment = async () => {
  if (!commentId.value) return;

  try {
    const deletedComment = await commentService.deleteComment(
      {
        id: commentId.value,
        title: props.name,
        content: content.value,
        team: { id: props.id_team, name: props.name_team },
        sprint: { id: props.id_sprint },
        emitter: { id: props.id_emitter },
      } as Comment);
    if (deletedComment) {
      resetCommentState();
    }
  } catch (error) {
    console.error("Error deleting comments.", error);
  }
};

const editComment = () => {
  isCommentSent.value = false;
  isCommentEdited.value = true;
};

const resetCommentState = () => {
  isCommentSent.value = false;
  isCommentEdited.value = false;
  content.value = '';
  commentId.value = undefined;
  commentDate.value = new Date();
};

const fetchComments = async () => {
  try {
    const comments = await commentService.getCommentsByTeamAndSprint(props.id_team, props.id_sprint);
    const specificComments = comments?.filter(c => c.title === props.name && c.emitter.id === props.id_emitter);

    if (specificComments && specificComments.length > 0) {
      const lastComment = specificComments[specificComments.length - 1];
      content.value = lastComment.content;
      commentId.value = lastComment.id;
      commentDate.value = lastComment.date ? new Date(lastComment.date) : new Date();
      isCommentSent.value = true;
    }
  } catch (error) {
    console.error("Error fetching comments.", error);
  }
};

onMounted(fetchComments);

watch(() => [props.id_team, props.id_sprint, props.id_emitter], async () => {
  resetCommentState();
  await fetchComments();
});

</script>

<template>
  <div>
    <div class="comment-section">
      <h2>Commentaires</h2>
      <n-input
        v-model:value="content"
        class="comment_content"
        type="textarea"
        :placeholder="placeholder"
        :autosize="{ minRows: 3 }"
        :disabled="isCommentSent"
      >
        <template #suffix>
          <n-button @click="createOrUpdateComment" :disabled="isCommentSent">Envoyer</n-button>
        </template>
      </n-input>

      <n-button v-if="result || isCommentSent" type="info" class="deleteComments" @click="deleteComment">Supprimer</n-button>
      <n-button v-if="isCommentSent" type="info" class="editComments" @click="editComment">Modifier</n-button>
    </div>
  </div>
</template>

<style scoped>
.comment-section {
  margin: 20px;
}

.comment-section h2 {
  margin-bottom: 10px;
}

.deleteComments, .editComments {
  margin-top: 10px;
  margin-right: 10px;
}
</style>
