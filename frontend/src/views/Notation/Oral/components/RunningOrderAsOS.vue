<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { teamService } from '@/services/team.service';
import { teamOrderService } from '@/services/teamOrder.service';
import { useUserStore } from '@/stores/useUserStore';
import type { Team, User } from '@/services/types';
import { toast } from 'vue3-toastify';
import Loading from '@/components/Loading.vue';
import draggable from 'vuedraggable';
import SprintSelector from '@/components/SprintSelector.vue';
import { sprintService } from '@/services/sprint.service';

const userStore = useUserStore();

const userInfo = ref<User | null>(null);
userInfo.value = userStore.getUser();

type MemberType = {
  id: number;
  firstName: string;
  lastName: string;
  selected: null | number;
};

const teamMembers = ref<MemberType[]>([]);
const activeUserId = userInfo.value?.id;
const orderedMembers = ref<MemberType[]>([]);
const unorderedMembers = ref<MemberType[]>([]);
const teamWithActiveUser = ref<any>();

const isButtonDisabled = computed(() => {
  const counts = orderedMembers.value.reduce(
    (acc: Record<number, number>, member) => {
      acc[member.id] = (acc[member.id] || 0) + 1;
      return acc;
    },
    {} as Record<number, number>
  );

  const allSelected = orderedMembers.value.length === teamMembers.value.length;

  return (
    Object.values(counts).some((count: number) => count > 1) || !allSelected
  );
});

const handleValidation = async () => {
  try {
    const teamId = teamWithActiveUser.value.id;
    const sprintId = selectedSprint.value;
    const userIds = orderedMembers.value.map(member => member.id);

    if (await teamOrderService.saveTeamOrder(teamId, sprintId ?? 0, userIds)) {
      toast.success('Ordre de passage enregistré avec succès');
    } else {
      toast.error("Erreur lors de l'enregistrement de l'ordre de passage");
    }
  } catch (error) {
    toast.error("Erreur lors de l'enregistrement de l'ordre de passage");
  }
};

const selectedSprint = computed(() =>
  useUserStore().getSelectedSprintId()
);

const fetchOrder = async () => {
  await updateIsSprintActive();
  if (!teamWithActiveUser.value) {
    return;
  }
  try {
    const teamId = teamWithActiveUser.value.id;
    const response = await teamOrderService.getTeamOrderByTeamIdAndSprintId(
      teamId,
      selectedSprint.value ?? 0
    );
    if (response && response.order) {
      const teamOrder = response.order;
      if (teamOrder.length === 0) {
        orderedMembers.value = [];
        return;
      }
      orderedMembers.value = teamOrder.map(
        (member: any) => ({
          ...member,
          selected: member.id
        })
      );
      unorderedMembers.value = teamMembers.value.filter(
        member => !orderedMembers.value.some(
          orderedMember => orderedMember.id === member.id
        )
      );
      console.log('Ordre de passage récupéré:', teamOrder);
    } else {
      console.log("Erreur lors de la récupération de l'ordre de passage");
    }
  } catch (error) {
    console.error('Erreur:', error);
  }
};

const isSprintActive = ref(false);

const updateIsSprintActive = async () => {
  const today = new Date();
  const selectedSprintData = await sprintService.getSprintById(selectedSprint.value ?? 0);
  isSprintActive.value = (
    selectedSprintData &&
    today >= selectedSprintData.startDate &&
    today <= selectedSprintData.endDate
  ) ?? false;
};

watch(selectedSprint, fetchOrder);

const fetchAll = async () => {
  const response = await teamService.getAll();
  let teams = ref<Team[]>([]);
  if (response) {
    teams.value = response;
  }

  teamWithActiveUser.value = teams.value.find((team) =>
    team.users.some((user) => user.id === activeUserId)
  );

  if (teamWithActiveUser.value) {
    try {
      const team = await teamService.getTeamById(teamWithActiveUser.value.id);
      teamMembers.value = team.users.map((member: any) => ({
        ...member,
        selected: null,
      }));
      unorderedMembers.value = [...teamMembers.value];
    } catch (error) {
      console.error(error);
    }
  }
  await fetchOrder();
};

const pageNotReady = ref(true);

onMounted(async () => {
  await fetchAll().finally(() => {
    pageNotReady.value = false;
  });
});
</script>

<template>
  <Loading v-if="pageNotReady" />
  <div v-else>
    <div class="header">
      <h1 class="header-title">Ordre de passage pour la présentation</h1>
      <div class="select-container">
        <SprintSelector />
      </div>
    </div>
    <div class="explications">
      <p class="small-text" v-if="isSprintActive">
        Veuillez rentrer votre ordre de passage en utilisant le drag and drop. Il vous faudra renseigner l'ordre de tous les étudiants avant de pouvoir valider.
      </p>
      <p class="small-text" v-else>
        {{ orderedMembers.length==0 ? '' : 'Voici l\'ordre qui a été défini pour la présentation de ce sprint' }}
      </p>
    </div>
    <div v-if="isSprintActive" class="drag-drop-container">
      <div class="column">
        <h2 class="category-title">Ordre défini</h2>
        <draggable
          v-model="orderedMembers"
          :group="{ name: 'members', put: true }"
          item-key="id"
        >
          <template #item="{ element, index }">
            <n-card class="draggable-item" size="small" :bordered="false">
              <span><i class="fa fa-bars draggable-icon"></i>  {{ index + 1 }}. {{ element.firstName }} {{ element.lastName }}</span>
            </n-card>
          </template>
        </draggable>
      </div>
      <div class="column">
        <h2 class="category-title">Liste des étudiants à ordonner</h2>
        <draggable
          v-model="unorderedMembers"
          :group="{ name: 'members', put: true }"
          item-key="id"
        >
          <template #item="{ element }">
            <n-card class="draggable-item" size="small" :bordered="false">
              <span><i class="fa fa-bars draggable-icon"></i>  {{ element.firstName }} {{ element.lastName }}</span>
            </n-card>
          </template>
        </draggable>
      </div>
    </div>
    <div class="grid-container" v-else>
      <div v-if="orderedMembers.length==0">
        <h1 class="no-order">L'ordre n'a pas été défini par l'équipe</h1>
      </div>
      <div class="grid">
        <n-card
          v-for="(member, index) in orderedMembers"
          :key="member.id"
          class="draggable-item"
        >
          <div class="info-container">
            <p>{{index + 1}}. {{ member.firstName }} {{ member.lastName }}</p>
          </div>
        </n-card>
      </div>
    </div>
    <footer class="footer" v-if="isSprintActive">
      <div class="button-container">
        <n-button :disabled="isButtonDisabled" @click="handleValidation">
          Valider
        </n-button>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  border-top: 1px solid #e0e0e0;
  padding: 15px;
}

.no-order {
  text-align: center;
  margin-top: 100px;
}
h1 {
  text-align: center;
  margin-top: 3%;
  font-size: 1.8em;
}

.category-title {
  color: #333;
  font-size: 1.2em;
  margin-bottom: 10px;
  text-align: center;
}

.draggable-item {
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  color: #333;
  margin-bottom: 10px;
}

.select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  margin-top: 10px;
}

.team-select {
  max-width: 300px;
  margin-left: 10px;
}

.drag-drop-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  margin-top: 20px;
  margin-bottom: 100px;
}

.column {
  width: 45%;
}

.n-card {
  margin-bottom: 10px;
}

.button-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

.column {
  width: 40%;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 5px;
}

.draggable-icon {
  color: #999;
  cursor: grab;
  margin-right: 30px;
}

.explications {
  text-align: center;
}

.small-text {
  font-style: italic;
  color: grey;
  font-size: 1em;
}

.header-title {
  margin-top: 20px;
}

.header {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.grid-container {
  margin-top: 20px;
  margin-bottom: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 40px;
  margin: 0 40px;
}
</style>
