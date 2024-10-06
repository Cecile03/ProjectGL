<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { sprintService } from '@/services/sprint.service';
import { toast } from 'vue3-toastify';
import { useUserStore } from '@/stores/useUserStore';
import QuickActions from '@/views/QuickActions.vue';
import Loading from '@/components/Loading.vue';
import { RocketOutline as RocketIcon } from '@vicons/ionicons5';

interface Sprint {
  id: number;
  startDate: Date | null;
  endDate: Date | null;
  endType: string;
}

const sprints = ref<Sprint[] | null>(null);
const allSprint = ref(false);
const userStore = useUserStore();
const userName = userStore.getUser()?.firstName;
let loading = ref(true);

const formatDate = (date: Date | null) => {
  if (date) {
    return new Date(date).toLocaleDateString('fr-FR');
  }
};

const fetchSprints = async () => {
  try {
    const sprintsRes = await sprintService.getAllSprints();
    if (sprintsRes && Array.isArray(sprintsRes)) {
      sprints.value = sprintsRes.map((sprint) => ({
        id: sprint.id,
        startDate: new Date(sprint.startDate),
        endDate: new Date(sprint.endDate),
        endType: sprint.endType,
      }));
      allSprint.value = true;
    }
  } catch (error) {
    toast.error("Erreur lors de l'affichage des sprints");
  }
};

const getSprintStatus = (sprint: Sprint) => {
  const now = new Date();
  if (sprint.startDate && sprint.endDate) {
    if (now >= sprint.startDate && now <= sprint.endDate) {
      return 'process';
    } else if (now > sprint.endDate) {
      return 'finish';
    }
  }
  return 'wait';
};

const getRemainingDays = () => {
  if (!sprints.value) return;
  const now = new Date();

  // Sort the sprints by start date
  const sortedSprints = [...sprints.value].sort((a, b) => {
    if (a.startDate && b.startDate) {
      return a.startDate.getTime() - b.startDate.getTime();
    }
    return 0;
  });

  // Find the last sprint that has started
  const currentSprint = sortedSprints.reverse().find((sprint) => {
    if (sprint.startDate) {
      return sprint.startDate <= now;
    }
    return false;
  });

  if (currentSprint && currentSprint.endDate) {
    const remainingTime = currentSprint.endDate.getTime() - now.getTime();
    const remainingDays = Math.ceil(remainingTime / (1000 * 60 * 60 * 24));
    if (remainingDays > 0) {
      return `Il reste ${remainingDays} jour(s) avant la fin du sprint en cours.`;
    } else {
      return "Le sprint en cours est terminé. Le prochain sprint n'a pas encore commencé.";
    }
  } else {
    // If no sprint has started yet, find the first sprint and calculate the remaining days
    const firstSprint = sortedSprints[0];
    if (firstSprint && firstSprint.startDate) {
      const remainingTime = firstSprint.startDate.getDate() - now.getDate();
      const remainingDays = Math.ceil(remainingTime / (1000 * 60 * 60 * 24));
      if (remainingDays > 0) {
        return `Il reste ${remainingDays} jour(s) avant le début du premier sprint.`;
      }
    }
    return 'Tous les sprints sont terminés.';
  }
};

const getCurrentSprintIndex = () => {
  if (!sprints.value) return;
  const now = new Date();
  return sprints.value.findIndex((sprint) => {
    if (sprint.startDate && sprint.endDate) {
      return sprint.startDate <= now && sprint.endDate >= now;
    }
    return false;
  });
};

onMounted(async () => {
  await fetchSprints();
  loading.value = false;
});
</script>

<template>
  <Loading v-if="loading" />
  <div v-else class="allSprints">
    <div class="header_welcome">
      <div class="header_info">
        <h1 style="color: var(--primaryColor)">Bonjour {{ userName }} !</h1>
        <p v-if="sprints">{{ getRemainingDays() }}</p>
        <p v-else>Aucun sprint n'a été créé.</p>
      </div>
    </div>
    <div class="steps" v-if="sprints">
      <img
        src="../assets/img/astronaute_accueil.png"
        alt="illustration-accueil" />
      <n-steps
        :current="getCurrentSprintIndex()"
        style="max-width: 800px; margin: auto; padding-top: 50px">
        <n-step
          v-for="(sprint, n) in sprints"
          :key="n"
          :title="'Sprint ' + (n + 1)"
          :status="getSprintStatus(sprint)">
          <div>
            <div
              style="display: flex; flex-direction: row; align-items: center">
              <i class="fas fa-cogs" style="margin-right: 5px"></i>
              <p>{{ sprint.endType }}</p>
            </div>
            <div
              style="display: flex; flex-direction: row; align-items: center">
              <i class="fa-regular fa-calendar" style="margin-right: 10px"></i>
              <p>
                {{ formatDate(sprint.startDate) }} -
                {{ formatDate(sprint.endDate) }}
              </p>
            </div>
          </div>
          <template #icon>
            <n-icon>
              <i class="fa-regular fa-clock"></i>
            </n-icon>
          </template>
        </n-step>
      </n-steps>
    </div>
    <div>
      <div class="quick-actions">
        <n-icon :component="RocketIcon" size="28" />
        <p class="text-qa">Actions rapides</p>
      </div>
      <QuickActions />
    </div>
  </div>
</template>

<style scoped>
.allSprints {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 100px;
}

.header_welcome {
  display: flex;
  justify-content: space-between; /* This will place the image to the left and the text to the right */
  width: 80%;
  padding-bottom: 15px;
  position: relative;
}

.header_welcome::after {
  content: '';
  position: absolute;
  left: 17%; /* Adjust this value as needed */
  right: 17%; /* Adjust this value as needed */
  bottom: 0;
  border-bottom: 1px solid #bdc3cb;
}

.header_info {
  display: flex;
  flex-direction: column;
  align-items: center; /* This will center the text vertically */
  justify-content: center;
  height: 100%; /* This will make the text take up the full height of the parent div */
  margin: auto; /* This will center the text horizontally */
}

img {
  position: absolute; /* This will take the image out of the normal document flow */
  top: -50px;
  left: -300px;
  width: 250px;
  height: 250px;
  height: auto;
}
.steps {
  position: relative;
}
.steps::after {
  content: '';
  display: block;
  padding-bottom: 50px;
  border-bottom: 1px solid #bdc3cb;
}
.text-qa {
  font-weight: 500;
  font-size: 1.3rem;
  margin-top: 10px;
  margin-bottom: 10px;
}

.quick-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px;
  gap: 5px;
  color: var(--textColor);
}
</style>
