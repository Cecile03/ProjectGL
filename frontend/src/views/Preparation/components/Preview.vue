<template>
  <div class="container">
    <n-grid x-gap="10" y-gap="20" :cols="3">
      <n-gi v-for="(team, index) in teamUsers" :key="index">
        <div class="team-header">
          <div class="team-title-container">
            <h2>Equipe {{ index + 1 }} : {{ team.name }}</h2>
          </div>
          <div class="popovers">
            <n-popover trigger="hover">
              <template #trigger>
                <n-icon :size="18" :component="team.validated ? CheckmarkCircleOutline : WarningOutline" :color="team.validated ? 'green' : 'red' "/>
              </template>
              <div>
                <span>{{ team.validated ? 'Equipe validée' : 'Equipe non validée' }}</span>
              </div>
            </n-popover>
            <n-popover trigger="hover">
              <template #trigger>
                <n-icon :size="18" :component="Info16Regular" />
              </template>
              <div>
                <div v-for="(checkbox, i) in generateCheckboxes(index)" :key="i">
                  <span>{{ checkbox.label }}</span>
                  <n-icon :component="checkbox.modelValue ? CheckmarkCircleOutline : WarningOutline" :color="checkbox.modelValue ? 'green' : 'red'" />
                </div>
              </div>
            </n-popover>
          </div>
        </div>
          <table>
            <thead>
            <tr>
              <th></th>
              <th>Nom Prenom</th>
              <th>M-F-B</th>
              <th>Moyenne</th>
            </tr>
            </thead>
            <draggable v-model="team.users" :group="{ name: 'people' }" tag="tbody" :itemKey="'id'" @end="handleDrag">
              <template v-slot:item="{element, index}">
                <tr :key="index">
                  <td><i class="fa fa-bars draggable-icon"></i></td>
                  <td>{{ element.name }}</td>
                  <td>{{ element.mfb }}</td>
                  <td>{{ element.average }}</td>
                </tr>
              </template>
            </draggable>
            <tfoot >
            <tr>
              <td class="end-row" colspan="1"><b>Moyenne</b></td>
              <td class="end-row" colspan="2"></td>
              <td class="end-row"><b>{{ calculateAverage(team.users) }}</b></td>
            </tr>
            </tfoot>
          </table>
        </n-gi>
      </n-grid>
  </div>
  <div class="button-container-wrapper">
    <div class="button-container">
      <n-button id="prepublish" v-if="!status" @click="prepublish">Prépublier</n-button>
      <n-button id="validate" v-if="!status" @click="publish">Publier</n-button>
      <n-button id="nextstep"  type="info" @click="handleValidation">Suivant</n-button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, h, onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import { teamService } from '@/services/team.service';
import type { Team, TeamData } from '@/services/types';
import Info16Regular from '@vicons/fluent/Info16Regular';
import type { DataTableCreateSummary } from 'naive-ui';
import draggable from 'vuedraggable';
import { CheckmarkCircleOutline, WarningOutline } from '@vicons/ionicons5';


interface User {
  id: number | undefined;
  name: string;
  mfb: string;
  average: number;
  team: number;
}

export default defineComponent({
  computed: {
    CheckmarkCircleOutline() {
      return CheckmarkCircleOutline
    },
    WarningOutline() {
      return WarningOutline
    }
  },
  components: {
    draggable,
  },
  setup(props, context) {
    // Variables
    const data = ref<Team[]>([]);
    const teamUsers = ref<TeamData[]>([]);

    onMounted(async () => {
      //Recupération des équipes avec les utilisateurs associés
      const res = await teamService.getAll();
      if (res) {
        data.value = res;
      }
      console.log('dataOnMounted', data.value);

      //Mise en forme
      teamUsers.value = formatData(data.value);
      console.log('teamUsersOnMounted', teamUsers);

      // Check if there is a toast message in the localStorage
      const toastMessage = localStorage.getItem('toastMessage');

      if (toastMessage) {
        // Display the toast
        toast.warn(toastMessage);

        // Remove the message from the localStorage
        localStorage.removeItem('toastMessage');
      }
    });

    const formatData = (teamsF: Team[]): TeamData[] => {
      if (!Array.isArray(teamsF)) {
        teamsF = [teamsF];
        toast.warn("Team is not an array");
      }

      return teamsF.map(team => {
        if (!team.users) {
          throw new Error(`Team ${team.id} has no users property`);
        }

        return {
          users: team.users.map(user => ({
            id: user.id,
            name: `${user.firstName} ${user.lastName}`,
            mfb: `${user.gender === 'male' ? 'M' : 'F'}${user.isBachelor ? ' - B' : ''}`,
            average: user.gradePast,
            team: team.id || 0,
          })),
          id: team.id,
          validated: team.validated,
          name: team.name,
          status: team.status,
          supervisor: team.supervisor,
          criteria: team.criteria
        };
      });
    };

    const calculateAverage = (users: User[]) => {
      const total = users.reduce((sum: number, user: User) => sum + user.average, 0);
      return (total / users.length).toFixed(2);
    };

    const saveTeams = async (teamsF: Team[]) => {
      console.log("équipes à sauvegarder", teamsF);
      try {
        // Save each TeamData object
        for (const team of teamsF) {
          const response = await teamService.saveTeam(team);
          console.log('response', response.data);
        }
        toast.success('Les changements ont été sauvegardés avec succès.');
      } catch (error) {
        toast.error('Erreur lors de l\'enregistrement des changements.');
      }

    };


    const prepublish = async () => {
      // Mettre à jour le statut de toutes les équipes à 'prepublish'
      data.value.forEach(team => team.status = 'prepublish');
      teamUsers.value.forEach(team => {
        team.status = 'prepublish';
      });
      // Sauvegarder les équipes avec le nouveau statut
      await saveTeams(data.value);
      updateTeamStatus();
    };

    const publish = async () => {
      // Mettre à jour le statut de toutes les équipes à 'published'
      data.value.forEach(team => {
        team.status = 'publish';
        team.validated = true;
      });
      teamUsers.value.forEach(team => {
        team.status = 'publish';
        team.validated = true;
      });

      // Sauvegarder les équipes avec le nouveau statut
      await saveTeams(data.value);
      updateTeamStatus();
      console.log('publish', data.value);
    };

    const handleValidation = async () => {
      context.emit('validate');
    };

    const handleDrag = async () => {
      // Mettre à jour les équipes dans le backend
      data.value = formatData(teamUsers.value);
      console.log("DRAG DATA : ", data.value)
      const reponse = await saveTeams(data.value);
      console.log('response of drag', reponse);
    };



    const countGirls = (users: any) => users.filter((user: any) => user.mfb.includes('F')).length;
    const countBachelors = (users: any) => users.filter((user: any) => user.mfb.includes('B')).length;

    const calculateDifferenceMinMaxTeamAverages = (teamsF : TeamData[]) => {
      if (!teamsF || teamsF.length === 0) {
        return 0;
      }
      if (!Array.isArray(teamsF)) {
        console.error('teams is not an array');
        return 0;
      }
      let teamAverages = teamsF.map(team => {
        let total = 0;
        for (const user of team.users) {
          total += user.average;
        }
        return total / team.users.length;
      });

      let minAverage = Math.min(...teamAverages);
      let maxAverage = Math.max(...teamAverages);

      return parseFloat((maxAverage - minAverage).toFixed(2));
    };

    const generateCheckboxes = (index: any) => {
      const team = teamUsers.value[index];

      const girlsCount = countGirls(team.users);
      const bachelorCount = countBachelors(team.users);
      const differenceAverages = calculateDifferenceMinMaxTeamAverages(teamUsers.value)
      const criteria = team.criteria;
      return [
        { label: "Nombre de filles: "+girlsCount+"/"+criteria.numberOfGirls+" ", modelValue: girlsCount==criteria.numberOfGirls },
        { label: "Nombre de bachelors: "+bachelorCount+"/"+criteria.numberOfBachelor+" ", modelValue: bachelorCount==criteria.numberOfBachelor },
        { label: "Ecart de moyennes: "+differenceAverages+"/"+criteria.minAverageThreshold+" ", modelValue: differenceAverages<=criteria.minAverageThreshold },
      ]
    };



    // Lifecycle hooks
    onMounted(async () => {
          //Recupération des équipes avec les utilisateurs associés
          const res = await teamService.getAll();
          if (res) {
            data.value = res;
          }
          console.log('dataOnMounted', data.value);

          //Mise en forme
          teamUsers.value = formatData(data.value);
          console.log('teamUsersOnMounted', teamUsers);

          // Check if there is a toast message in the localStorage
          const toastMessage = localStorage.getItem('toastMessage');

          if (toastMessage) {
            // Display the toast
            toast.warn(toastMessage);

            // Remove the message from the localStorage
            localStorage.removeItem('toastMessage');
          }
          updateTeamStatus();
        }
    );

    // Summary
    const summary: DataTableCreateSummary = (pageData: any) => {
      const total = pageData.reduce((prevValue: any, row: any) => prevValue + row.average, 0);
      const average = total / pageData.length;
      return {
        name: { value: h('span', { style: { color: 'black' } }, 'Moyenne'), colSpan: 1 },
        average: { value: h('span', { style: { color: 'black' } }, average.toFixed(2)), colSpan: 4 },
      };
    };

    const status = ref();
    const updateTeamStatus = () => {
      status.value = data.value.every((item) => item.validated);
    };

    // Return
    return {
      publish,
      prepublish,
      data,
      summary,
      value: ref(null),
      isEditing: false,
      teamUsers,
      saveTeams,
      handleValidation,
      Info16Regular,
      calculateAverage,
      generateCheckboxes,
      handleDrag,
      status
    };
  },
});
</script>

<style scoped>
.container {
  width: 100%;
  margin-bottom: 40px;
}

.button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  padding: 10px;
  gap: 10px;
  background-color: white;
}

.draggable-icon {
  cursor: grab;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
  border: 1px solid #ddd;
  border-radius: 100px ;
}

table th, table td {
  padding: 8px;
  text-align: center;
  border-bottom: 1px solid #ddd;
  border-top: 1px solid #ddd;
}

table th, .end-row{
  background-color: #f2f2f2;
  color: black;
}

table td {
  background-color: #fff;
}

.team-header {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  position: relative;
  margin-bottom: 20px;
}

.team-title-container {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
}

.popovers {
  gap: 10px;
  position: absolute;
  right: 0;
}
</style>