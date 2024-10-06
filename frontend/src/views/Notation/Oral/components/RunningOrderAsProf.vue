<script lang="ts">
import { defineComponent, onMounted, ref, watch } from 'vue';
import { teamService } from '@/services/team.service';
import { teamOrderService } from '@/services/teamOrder.service';
import { toast } from 'vue3-toastify';
import { useUserStore } from '@/stores/useUserStore';
import type { Team, User } from '@/services/types';
import Loading from '@/components/Loading.vue';
import NCard from 'naive-ui/lib/card';

export default defineComponent({
  components: { Loading, NCard },
  props: {
    teamId: {
      type: Number,
      required: true,
    },
    sprintId: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const userStore = useUserStore();

    const userInfo = ref<User | null>(null);
    userInfo.value = userStore.getUser();

    const currentTeam = ref<Team | null>(null);

    const orderedMembers = ref<User[]>([]);
    const isEmpty = ref<boolean>(false);

    const fetchOrder = async () => {
      if (!props.teamId || !props.sprintId) {
        return;
      }
      try {
        const response = await teamOrderService.getTeamOrderByTeamIdAndSprintId(
          props.teamId,
          props.sprintId
        );
        console.log('Response:', response)
        if (response && response.order) {
          const teamOrder = response.order;
          if (teamOrder.length === 0) {
            isEmpty.value = true;
            return;
          }
          isEmpty.value = false;
          orderedMembers.value = teamOrder.map((member: User) => ({
            ...member,
            selected: member.id,
          }));
          console.log('Ordre de passage récupéré:', teamOrder);
        } else {
          toast.error("Erreur lors de la récupération de l'ordre de passage");
        }
      } catch (error) {
        console.error('Erreur:', error);
        toast.error('Erreur lors de la récupération de l\'ordre de passage');
      }
    };

    const fetchAll = async () => {
      console.log('fetchAll', props.sprintId, props.teamId);
      if (props.teamId) {
        try {
          currentTeam.value = await teamService.getTeamById(props.teamId);
        } catch (error) {
          console.error(error);
          toast.error('Erreur lors de la récupération de l\'équipe');
        }
      }
      console.log('fetch Order');
      await fetchOrder();
      console.log('fetch done');
    };

    const pageNotReady = ref(true);

    watch(
      () => [props.teamId, props.sprintId],
      async ([newSprintId, newTeamId], [oldSprintId, oldTeamId]) => {
        if (newSprintId !== oldSprintId || newTeamId !== oldTeamId) {
          await fetchAll();
        }
      }
    );

    onMounted(async () => {
      await fetchAll().finally(() => {
        pageNotReady.value = false;
      });
    });

    return {
      currentTeam,
      orderedMembers,
      pageNotReady,
      isEmpty
    };
  },
});
</script>

<template>
  <Loading v-if="pageNotReady" />
  <div v-else>
    <div v-if="isEmpty" class="no-order-title">
      <h1 class="no-order">L'ordre n'a pas encore été défini par l'équipe</h1>
    </div>
    <div v-else>
      <div class="header">
        <h1>Ordre de passage pour la présentation - {{ currentTeam?.name }}</h1>
      </div>
      <div class="explications">
        <p class="small-text">
          Ici est affiché l'ordre de passage de l'équipe préalablement sélectionnée.
        </p>
      </div>
      <div class="grid-container">
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
    </div>
  </div>
</template>

<style scoped>
h1 {
  text-align: center;
  margin-top: 3%;
  font-size: 1.8em;
}

.no-order-title {
  color: #333;
  font-size: 1.2em;
  text-align: center;
}

.draggable-item {
  background-color: #f9f9f9;
  margin-bottom: 10px;
}

.grid-container {
  margin-top: 20px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 40px;
  margin: 0 40px;
}

.explications {
  text-align: center;
}

.small-text {
  font-style: italic;
  color: grey;
  font-size: 1em;
}

.no-order {
  text-align: center;
  margin-top: 200px;
}
</style>
