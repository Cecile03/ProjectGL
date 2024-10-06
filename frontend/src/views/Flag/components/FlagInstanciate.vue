<template>
  <div v-if="loading">
    <Loading></Loading>
  </div>
  <div v-else>
    <Breadcrumb class="breadcrumb"/>
    <div class="tables">
      <n-grid x-gap="10" y-gap="20" :cols="3">
        <n-gi v-for="(team, index) in teamUsers" :key="index">
          <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2 style="text-align: center; flex-grow: 1;">
          Equipe {{ index + 1 }} : {{ team.name }}
        </h2>
        <n-popover trigger="hover">
          <template #trigger>
            <n-icon :size="18" :component="Info16Regular" />
          </template>
          <div>
            <div v-for="(checkbox, i) in generateCheckboxes(index)" :key="i">
              <span>{{ checkbox.label }}</span>
              <n-icon :component="checkbox.modelValue ? CheckCircleFill : ErrorCircleFill" :color="checkbox.modelValue ? 'green' : 'red'" />
            </div>
          </div>
        </n-popover>
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
        </table>
      </n-gi>
    </n-grid>
  </div>
  </div>
  
  <div class="buttons">
    <n-button @click="cancelFlagInstanciate" type="info">Annuler le signalement</n-button>
    <n-button @click="saveTeams">Valider le signalement</n-button>
  </div>
</template>

<script lang="ts">
import type { DataTableCreateSummary } from 'naive-ui';
import { useDialog  } from 'naive-ui';
import { defineComponent, h, onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import { type User, type UserData, type Flag, type Team, type TeamData, type NotificationSend } from '@/services/types';
import { teamService } from '@/services/team.service';
import { useUserStore } from '@/stores/useUserStore';
import { flagService } from '@/services/flag.service';
import { type FlagResponse } from '@/services/flag.service';
import Info16Regular from '@vicons/fluent/Info16Regular';
import CheckCircleFill from '@vicons/fluent/CheckmarkCircle12Filled';
import ErrorCircleFill from '@vicons/fluent/ErrorCircle12Filled';
import Loading from '@/components/Loading.vue';
import { notificationService } from '@/services/notification.service';
import { userService } from '@/services/user.service';
import draggable from 'vuedraggable';


const createSummary: DataTableCreateSummary = (pageData) => {
const total = pageData.reduce((prevValue: any, row: any) => prevValue + row.average, 0);
const average = total / pageData.length;

return {
  name: {
    value: h('span', { style: { color: 'black' } }, 'Moyenne'),
    colSpan: 1,
  },
  average: {
    value: h('span', { style: { color: 'black' } }, average.toFixed(2)),
    colSpan: 4,
  },
};
};

export default defineComponent({
emits: ['validate'],
components: {
    draggable,
  },
setup() {
  //Instanciation des variables
  let data = ref<Team[]>([]);
  let teamUsers = ref<TeamData[]>([]);
  const dialog = useDialog();
  const comment = ref('');
  let userFlag: User = useUserStore().getUser() as User;
  let datetimeFlag = new Date();
  let loading = ref(false);

  //Appel des fonctions asyncrones
  onMounted(async () => {
    loading.value = true;
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
    loading.value = false;
  }
  );
  
  const getTeamNameById = (id: number) => {
    const team = data.value.find((team) => team.id === id);
    return team ? team.name : 'Équipe non trouvée';
  };
  
  const formatData = (teams: Team[]): TeamData[] => {
    if (!Array.isArray(teams)) {
      teams = [teams];
      toast.warn("Team is not an array");
    }

    return teams.map(team => {
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
        name: team.name,
        status: team.status,
        supervisor: team.supervisor,
        criteria: team.criteria
      };
    });
  };

  const unformatData = (teamDatas: TeamData[]): Team[] => {
    if (!Array.isArray(teamDatas)) {
      teamDatas = [teamDatas];
      toast.warn("TeamData is not an array");
    }

    return teamDatas.map(teamData => {
      if (!teamData.users) {
        throw new Error(`TeamData ${teamData.id} has no users property`);
      }

      return {
        id: teamData.id,
        name: teamData.name,
        status: teamData.status,
        supervisor: teamData.supervisor,
        users: teamData.users.map(userData => ({
          id: userData.id,
          firstName: userData.name.split(' ')[0],
          lastName: userData.name.split(' ')[1],
          gender: userData.mfb[0] === 'M' ? 'male' : 'female',
          isBachelor: userData.mfb.includes('B'),
          gradePast: userData.average,
        })),
        criteria: teamData.criteria
      };
    });
  };

  const saveTeams = async () => {
    let dataDb= ref<Team[]>([]);
    const res = await teamService.getAll()
    if (res) {
      dataDb.value = res;
    }

    let teamUsersDb: TeamData[] = formatData(dataDb.value);
    console.log("teamUserssers dans la BDD", teamUsersDb)

    console.log("teamUsers dans le tableau", teamUsers.value)

    let usersChanged: User[] = [];
    let messages: string[] = [];
    const affectedTeams: Set<number> = new Set();
    
    reassignTeamIds(teamUsers.value);
    
    teamUsersDb.forEach((teamDb: TeamData) => {
      teamDb.users.forEach((userDb: UserData) => {
        const user = teamUsers.value.flatMap(team => team.users).find((user: UserData) => user.id === userDb.id);
        
        if (user && user.team !== userDb.team) {
          messages.push(`${userDb.name} : équipe ${teamDb.name} --> équipe ${getTeamNameById(user.team)}`);
          usersChanged.push(formatUserData(userDb, data.value));
          console.log("user selectionné", user);
          
          affectedTeams.add(teamDb.id??0);
          affectedTeams.add(user.team);
        }
      });
    });

    //Si un flag affecte plus de 2 équipes, afficher un message d'erreur
    if (affectedTeams.size > 2) {
      // Store the toast message in the localStorage
      localStorage.setItem('toastMessage', 'Un flag ne peut pas affecter plus de 2 équipes');

      // Reload the page
      window.location.reload(); 
      return;
    }

    if (userFlag.roles.includes('OS')) {
      let currentUserTeamId = null;
      for (let team of data.value) {
        if (team.users.some(user => user.id === userFlag.id)) {
          currentUserTeamId = team.id;
          break;
        }
      }
      
      if (currentUserTeamId === null || !affectedTeams.has(currentUserTeamId)) {
        console.log("currentUserTeamId", currentUserTeamId);
        // Store the toast message in the localStorage
        localStorage.setItem('toastMessage', 'Vous ne pouvez pas signaler un changement qui ne concerne pas votre équipe');
      
        // Reload the page
        window.location.reload();
        return;
      }
    }
    //Verif si le flag concerne l'utilisateur
    


    //Si des changements sont détectés, afficher une boîte de dialogue
    if (messages.length > 0) {
      dialog.create({
        title: 'Voulez vous signaler les changements suivants ?',
        content: () => h('div', {style: 'display: flex; flex-direction: column; gap: 10px;'}, [
          h('p', {}, messages.join('\n')),
          h('input', {
            type: 'text',
            placeholder: 'Pourquoi effectuez vous ces changements ?',
            value: comment.value,
            onInput: (event: Event) => {
              comment.value = (event.target as HTMLInputElement).value;
            },
          }),
        ]),
        positiveText: 'Valider',
        negativeText: 'Annuler',
        onPositiveClick: () => {
          const affectedTeamsIterator = affectedTeams.values();
          const team1 = affectedTeamsIterator.next().value;
          const team2 = affectedTeamsIterator.next().value;

          let flagCreated: Flag = {
            user: userFlag,
            team1: team1,
            team2: team2,
            datetime: datetimeFlag,
            comment: comment.value,
          };
          saveFlag(flagCreated, usersChanged);
        },
        onNegativeClick: () => {
          cancelFlagInstanciate();
        },
      });
    } else {
      toast.warn('Aucun changement à signaler');
    }
  };

  const reassignTeamIds = (teamUsers: TeamData[]) => {
    teamUsers.forEach((team: TeamData) => {
      team.users.forEach((user: UserData) => {
        user.team = team.id ?? 0;
      });
    });
  };
  

  const formatUserData = (userData: UserData, teams: Team[]): User => {
    const team = teams.find(team => team.id === userData.team);
    const nameParts = userData.name.split(' ');
    const firstName = nameParts.slice(0, -1).join(' ');
    const lastName = nameParts[nameParts.length - 1];
    const gender = userData.mfb.startsWith('M') ? 'male' : 'female';
    const bachelor = userData.mfb.includes('B');
  
    return {
      id: userData.id,
      firstName: firstName,
      lastName: lastName,
      email: '', // Email is not provided in UserData
      roles: [], // Authorities are not provided in UserData
      gender: gender,
      option: '', // Option is not provided in UserData
      isBachelor: bachelor,
      gradePast: userData.average,
      team: team || undefined,
    };
  };

  /**
   * Méthode permettant de sauvegarder un Flag, de l'assigner à des utilisateurs avec UserFlag,
   * et de créer une notification pour chaque utilisateur affecté
   * @param flag 
   * @param usersChanged 
   */
  const saveFlag = async (flag: Flag, usersChanged: User[]) => {
    const flagDTO: FlagResponse = {
      id: flag.id ?? 0,
      userId: flag.user.id ?? 0,
      team1Id: flag.team1,
      team2Id: flag.team2,
      comment: flag.comment,
      datetime: flag.datetime,
    };
    console.log("flagDTO", flagDTO);
    
    
    const response = await flagService.createFlag(flagDTO);
    if (response){
      const flagCreated: FlagResponse = response;
      const resUF = await flagService.createUserFlag(flagCreated, usersChanged);
      toast.success('Votre signalement a bien été enregistré et une notification a été envoyée aux utilisateurs concernés ! ');
      if (resUF) {
        const userFlags = resUF;
        for (let uF of userFlags) {
          const notification: NotificationSend =  {
            type: 'Signalement',
            status: 'UNREAD',
            description: `Le signalement n°${flagCreated.id} concernant l'équipe ${flagCreated.team1Id.name} et l'équipe ${flagCreated.team2Id.name} a été créé par ${flag.user.firstName} ${flag.user.lastName}`,
            emitterId: flag.user.id?flag.user.id:0,
            receiverId: uF.userId.id?uF.userId.id:0,
            date: new Date(),
          }
          console.log("notification", notification);
          let notifRes: any;
          if (notification.emitterId != notification.receiverId) {
            notifRes = await notificationService.createNotification(notification);
          }
          else {
            const ownNotification: NotificationSend =  {
              type: 'Signalement',
              status: 'UNREAD',
              description: `Vous avez créé le signalement n°${flagCreated.id} qui concerne l'équipe ${flagCreated.team1Id.name} et l'équipe ${flagCreated.team2Id.name}`,
              emitterId: uF.userId.id?uF.userId.id:0,
              receiverId: uF.userId.id?uF.userId.id:0,
              date: new Date(),
            }
            notifRes = await notificationService.createNotification(ownNotification);
          }
          if (notifRes) {
            console.log("notification", notifRes);
          }
        }
        const teachers: User[] = await userService.getStaff();
        if (teachers) {
          for (let teacher of teachers) {
            const notification: NotificationSend =  {
              type: 'Signalement',
              status: 'UNREAD',
              description: `Le signalement n°${flagCreated.id} concernant l'équipe ${flagCreated.team1Id.name} et l'équipe ${flagCreated.team2Id.name} a été créé par ${flag.user.firstName} ${flag.user.lastName}`,
              emitterId: flag.user.id?flag.user.id:0,
              receiverId: teacher.id?teacher.id:0,
              date: new Date(),
            }
            console.log("notification", notification);
            const notifRes = await notificationService.createNotification(notification);
            if (notifRes) {
              console.log("notification", notifRes);
            }
          }

        }
      }
    }
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

  const cancelFlagInstanciate = () => {
    // Store the toast message in the localStorage
    localStorage.setItem('toastMessage', 'Signalement annulé');

    // Reload the page
    window.location.reload();
  };

  const handleDrag = async () => {
      // Mettre à jour les équipes dans le backend
      console.log("teamUsers DRAG DATA : ", teamUsers.value);
      data.value = unformatData(teamUsers.value);
      console.log("DRAG DATA : ", data.value);
    };

  return {
    data,
    summary: createSummary,
    value: ref(null),
    teamUsers,
    saveTeams,
    cancelFlagInstanciate,
    Info16Regular,
    CheckCircleFill,
    ErrorCircleFill,
    generateCheckboxes,
    loading,
    Loading,
    handleDrag
  };
},
});
</script>

<style scoped>
.buttons {
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

.tables {
display: flex;
padding: 20px;
margin-bottom: 40px;
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

table th {
  background-color: #f2f2f2;
  color: black;
}

table td {
  background-color: #fff;
}

.breadcrumb {
  margin-left: 10px;
  margin-top: 70px;
}

</style>