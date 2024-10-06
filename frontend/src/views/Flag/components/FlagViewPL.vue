<template>
    <Loading v-if="loading"/>
    <Breadcrumb class="breadcrumb"/>
    <Empty v-if="flags.length == 0" 
        title="Aucun signalement"
        subtitle="Il n'y a aucun signalement à afficher"
        :icon="FlagOutline"
        path="/flag"
        bouton-string="Retour"
    />
    <div v-if="loading == false && flags.length > 0" class="container">     
        <n-card class="menu">
            <n-menu
                :inverted="inverted"
                :options="menuOptions"
            />
        </n-card>
        <div v-if="selectedFlag" class="display-container">
            <n-card class="left-container">
                <h3>Informations sur le signalement : </h3>
                <div class="icon-text">
                    <n-icon :size="24" :component="TimeOutline" />
                    <span>{{ formatDate(selectedFlag.datetime) }}</span>
                </div>
                <div class="icon-text">
                    <n-icon :size="24" :component="BodyOutline" />
                    <n-tag 
                        :bordered="false" 
                        :round="true"
                        :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
                    >
                        <template #icon>
                            <n-icon :size="16" :component="PersonOutline" />
                        </template>
                        {{ selectedFlag.user.firstName }} {{ selectedFlag.user.lastName }}
                    </n-tag>
                </div>
                <div class="icon-text">
                    <n-icon :size="24" :component="ChatboxOutline" />
                    <span>{{ selectedFlag.comment }}</span>
                </div>
                <div class="icon-text">
                    <n-icon :size="24" :component="PeopleOutline" />
                    <span>Equipes </span>
                    <n-tag 
                        :bordered="false" 
                        :round="true"
                        :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
                    >
                        <template #icon>
                            <n-icon :size="16" :component="PeopleOutline" />
                        </template>
                    {{ selectedFlag.team1.name }}
                    </n-tag>
                    <span> et </span>
                    <n-tag 
                        :bordered="false" 
                        :round="true"
                        :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
                    >
                        <template #icon>
                            <n-icon :size="16" :component="PeopleOutline" />
                        </template>
                    {{ selectedFlag.team2.name }}
                    </n-tag>
                </div>  
                <div class="vote">
                    <div class="icon-text" v-if="isFlagValidated">
                        <n-icon :size="24" :component="Vote24Regular" />
                        <span>Voulez-vous valider ou refuser les changements émits par le signalement ? </span>
                    </div>
                    <div class="icon-text" v-else>
                        <n-icon :size="24" :component="ErrorCircleFill" />
                        <span>Vous devez attendre que tous les élèves aient accepté le signalement pour le valider. Une fois effectué, le changement prendra effet.</span>
                    </div>
                    <div class="vote-buttons" >
                        <div>
                            <n-popconfirm
                                @positive-click="validateFlag"
                                @negative-click="cancelVote"
                                negative-text="Annuler"
                                positive-text="Valider"
                                :negative-button-props="{ type: 'info' }"
                                :positive-button-props="{ type: 'default' }"
                            >
                                <template #trigger>
                                <n-button :disabled="!isFlagValidated">Valider</n-button>
                                </template>
                                Si vous validez le signalement, les changements seront effectués. Voulez vous confirmer ?
                            </n-popconfirm>
                        </div>
                        <n-popconfirm
                            @positive-click="openDialog"
                            @negative-click="cancelVote"
                            negative-text="Annuler"
                            positive-text="Confirmer"
                            :negative-button-props="{ type: 'info' }"
                            :positive-button-props="{ type: 'default' }"
                        >
                            <template #trigger>
                                <n-button type="info">Refuser</n-button>
                            </template>
                            En validant votre vote de refus, le signalement sera supprimé. Voulez vous confirmer ?
                        </n-popconfirm>
                    </div>
                </div>
            </n-card>
            <n-card class="right-container">
                <h3>Elèves concernés par le signalement </h3>
                <div v-for="userFlag in sortedUserFlags" :key="userFlag.id">
                    <div class="name-container">
                        <n-tooltip trigger="hover">
                            <template #trigger>
                                <n-icon :component="getIcon(userFlag.validated)" :color="getColor(userFlag.validated)" />
                            </template>
                            <div>{{ getStatus(userFlag.validated) }}</div>
                        </n-tooltip>
                        <div :style="{ color: userFlag.teamSwitched ? '#0087e0' : '' }">
                            {{ userFlag.user.firstName }} {{ userFlag.user.lastName }}
                        </div>
                    </div>  
                    <div v-if="userFlag.teamSwitched">
                        <div style="margin-left: 20px;" class="icon-text">
                            <n-icon :size="24" :component="ArrowUndoOutline" />
                            <span>Equipe d'origine : </span>
                            <n-tag 
                                :bordered="false" 
                                :round="true"
                                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
                            >
                                <template #icon>
                                    <n-icon :size="16" :component="PeopleOutline" />
                                </template>
                                {{ getOriginalTeam(userFlag).name }}
                            </n-tag>
                        </div>
                        <div style="margin-left: 20px;" class="icon-text">
                            <n-icon :size="24" :component="ArrowRedoOutline" />
                            <span>Equipe après changement : </span>
                            <n-tag 
                                :bordered="false" 
                                :round="true"
                                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
                            >
                                <template #icon>
                                    <n-icon :size="16" :component="PeopleOutline" />
                                </template>
                            {{ getChangedTeam(userFlag).name }} 
                        </n-tag>
                        </div>
                    </div>
                </div>
            </n-card>
        </div>
        <div v-else class="display-container" >
            <n-card>
                <span class="no-flags">Cliquez sur un signalement pour afficher les détails</span>
            </n-card>
        </div>
    </div>
    
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, computed, h, type Component } from 'vue';
import { flagService } from '@/services/flag.service';
import { teamService } from '@/services/team.service';
import { userService } from '@/services/user.service';
import { toast } from 'vue3-toastify';
import { type Flag, type UserFlag, type User, type NotificationSend, type Team } from '@/services/types';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useUserStore } from '@/stores/useUserStore';
import CheckCircleFill from '@vicons/fluent/CheckmarkCircle12Filled';
import ErrorCircleFill from '@vicons/fluent/ErrorCircle12Filled';
import SyncStatusSolid from '@vicons/fluent/AccessTime24Filled';
import Loading from '@/components/Loading.vue';
import Empty from '@/components/Empty.vue';
import { notificationService } from '@/services/notification.service';
import Vote24Regular from '@vicons/fluent/Vote24Regular';
import {
    FlagOutline,
    TimeOutline,
    ChatboxOutline,
    BodyOutline,
    PeopleOutline,
    ArrowUndoOutline,
    ArrowRedoOutline,
    PersonOutline
} from '@vicons/ionicons5'
import { NIcon, useDialog} from 'naive-ui';


export default defineComponent({
    setup() {
        const flags = ref<Flag[]>([]);
        const selectedFlag = ref<Flag | null>(null);
        const userFlags = ref<UserFlag[]>([]);
        let currentUser: User = useUserStore().getUser() as User;
        const dialog = useDialog(); 
        let loading = ref(false);
        const comment = ref('');
        let isFlagValidated = ref(false);
        
        const sortedUserFlags = computed(() => {
            return [...userFlags.value].sort((a, b) => Number(b.teamSwitched) - Number(a.teamSwitched));
        });

        const menuOptions = computed(() => flags.value.map(flag => ({
            label: `Signalement n°${flag.id}`,
            key: flag.id,
            icon: renderIcon(FlagOutline),
            onClick: () => handleClick(flag?.id ?? 0)
        })));

        function renderIcon (icon: Component) {
            return () => h(NIcon, null, { default: () => h(icon) })
        }


        onMounted(async () => {
            loading.value = true;
            const result = await flagService.getFlags();
            console.log("flags", result);
            if (result) {
                flags.value = await Promise.all(result.map(async (flag) => {
                    const userFlags = await flagService.getUserFlagByFlagId(flag.id ?? 0);
                    console.log("userFlags", userFlags);
                    return { ...flag, userFlags };
                }));
            } else {
                toast.error('Aucun flag detecté');
            }
            loading.value = false;

            const toastMessage = localStorage.getItem('toastMessage');
            if (toastMessage) {
                toast.success(toastMessage);
                localStorage.removeItem('toastMessage');
            }

            const lastFlag = localStorage.getItem('lastFlag');
            if (lastFlag) {
                handleClick(JSON.parse(lastFlag).id);
                localStorage.removeItem('lastFlag');
            }

        });

        const isFlagValidatedById = async (id: number) => {
            const reponse = await flagService.areAllUserFlagsValidated(id);
            console.log("reponse", reponse);
            if (reponse == true) {
                return true; 
            }
            return false;
        };  

        const handleClick = async (key: number) => {
            const flag: Flag | null = flags.value.find(flag => flag.id === key) || null;
            selectedFlag.value = flag;
            isFlagValidated.value = await isFlagValidatedById(flag?.id ?? 0);
            if (selectedFlag.value) {
                const result = await flagService.getUserFlagByFlagId(flag?.id ?? 0);
                if (result) {
                    userFlags.value = result;
                    console.log("userFlags", userFlags);
                }
            }
            

        };

        const getOriginalTeam = (userFlag: UserFlag) => {
            return userFlag.flag.team1.users.some(user => user.id === userFlag.user.id) ? userFlag.flag.team1 : userFlag.flag.team2;
        };

        const getChangedTeam = (userFlag: UserFlag) => {
            return getOriginalTeam(userFlag) === userFlag.flag.team1 ? userFlag.flag.team2 : userFlag.flag.team1;
        };

        const formatDate = (date: Date) => {
            return format(new Date(date), 'dd MMMM yyyy à HH:mm', { locale: fr });
        };

        const formatTeamsSwitched = (userFlags: UserFlag[]) => {
            let team1 = selectedFlag?.value?.team1 ? {...selectedFlag.value.team1} : null;
            let team2 = selectedFlag.value ? { ...selectedFlag.value.team2 } : null;
            return Array.isArray(userFlags) ? userFlags.filter((userFlag: UserFlag) => userFlag.teamSwitched).map(userFlag => {
                let oldTeam, newTeam;
                if (team1?.users.some(user => user.id === userFlag.user.id)) {
                    oldTeam = team1;
                    newTeam = team2;
                } else if (team2?.users.some(user => user.id === userFlag.user.id)) {
                    oldTeam = team2;
                    newTeam = team1;
                }
                return `${userFlag.user.firstName} ${userFlag.user.lastName} : ${oldTeam?.name} -> ${newTeam?.name}`;
            }).join(', ') : '';
        };

        const saveTeams = async (teamsF: Team[]) => {
            console.log("équipes à sauvegarder", teamsF);
            try {
                for (const team of teamsF) {
                const response = await teamService.saveTeam(team);
                console.log('response', response.data);
                }
                toast.success('Le changement d\'équipe a été effectué.');
            } catch (error) {
                toast.error('Erreur lors du changement d\'équipe.');
            }

        };

        const validateFlag = async () => {
            let isOk = false;
            try {
                const userFlagsConcerned: UserFlag[] = userFlags.value.filter(uf => uf.flag.id === selectedFlag.value?.id);
                console.log("userFlags concernés", userFlagsConcerned)
                const teamsSwitched = teamChanged(userFlagsConcerned);
                console.log("teamsSwitched", teamsSwitched);
                console.log("formatTeamsSwitched", formatTeamsSwitched(userFlagsConcerned))
                
                if (teamsSwitched != null && teamsSwitched.length > 0) {
                    const response = await saveTeams(teamsSwitched);
                    
                    console.log("reponse de saveTEams", response);
                    const allUsers = await userService.getUsers();
                    console.log("allUsers", allUsers)
                    for (const user of allUsers) {
                        const notification: NotificationSend =  {
                            type: 'Signalement',
                            status: 'UNREAD',
                            description: `${currentUser.firstName} ${currentUser.lastName} a validé le signalement n°${selectedFlag.value?.id} qui enregistre les changements : ${formatTeamsSwitched(userFlagsConcerned)}` ,
                            emitterId: currentUser.id?currentUser.id:0,
                            receiverId: user.id?user.id:0,
                            date: new Date(),
                        }
                        let notifRes: any;
                        if (notification.emitterId != notification.receiverId) {
                            notifRes = await notificationService.createNotification(notification);
                            
                        }
                        else {
                            const ownNotification: NotificationSend =  {
                                type: 'Signalement',
                                status: 'UNREAD',
                                description: `Vous avez validé le signalement n°${selectedFlag.value?.id} qui enregistre les changements : ${formatTeamsSwitched(userFlagsConcerned)}` ,
                                emitterId: currentUser.id?currentUser.id:0,
                                receiverId: user.id?user.id:0,
                                date: new Date(),
                            }
                            notifRes = await notificationService.createNotification(ownNotification);
                        }
                        if (notifRes) {
                            console.log("notification", notifRes);
                            isOk = true;
                        }
                        else {
                            //emitVote('Erreur lors de l envoi de notification');
                            console.log("Erreur lors de l envoi de notification");
                            isOk = false;
                        }
                    }
                    
                }
            } catch (error) {
                //emitVote('Erreur lors de la validation du signalement'); 
                console.log("Erreur lors de la validation du signalement");
            } finally {
                if (isOk) {
                    await flagService.deleteFlagById(selectedFlag.value?.id ?? 0);
                    console.log("flag deleted : ", selectedFlag.value);
                }
            }
            return false;
        };

        const teamChanged = (userFlags: UserFlag[]): Team[] => {
            if (!selectedFlag.value) {
                return [];
            }
        
            let team1 = {...selectedFlag.value.team1};
            let team2 = {...selectedFlag.value.team2};
        
            userFlags.forEach(userFlag => {
                if (userFlag.teamSwitched) {
                    if (team1.users.some(user => user.id === userFlag.user.id)) {
                        // Remove from team1 and add to team2
                        team1.users = team1.users.filter(user => user.id !== userFlag.user.id);
                        team2.users.push(userFlag.user);
                    } else if (team2.users.some(user => user.id === userFlag.user.id)) {
                        // Remove from team2 and add to team1
                        team2.users = team2.users.filter(user => user.id !== userFlag.user.id);
                        team1.users.push(userFlag.user);
                    }
                }
            });
        
            return [team1, team2];
        };

        const openDialog = () => {
            dialog.create({
                title: 'Message de refus',
                content: () => h('div', {style: 'display: flex; flex-direction: column; gap: 10px;'}, [
                    h('p', {}, 'Veuillez indiquer la raison du refus du signalement'),
                    h('input', {
                        type: 'text',
                        value: comment.value,
                        onInput: (event: Event) => {
                            comment.value = (event.target as HTMLInputElement).value;
                        },
                    }),
                ]),
                positiveText: 'Confirmer',
                negativeText: 'Annuler',
                onPositiveClick: () => cancelFlag(),
            });
        };

        const cancelFlag = async () => {
            try {
                const userFlag = userFlags.value.find(uf => uf.flag.id === selectedFlag.value?.id);
                if (userFlag) {
                    await flagService.deleteFlagById(userFlag.flag.id ?? 0);
                } else {
                    toast.error('Aucun signalement correspondant trouvé');
                }
            } catch (error) {
                toast.error('Erreur lors du refus du signalement');
            } finally {
                const userFlagsConcerned: UserFlag[] = userFlags.value.filter(uf => uf.flag.id === selectedFlag.value?.id);
                console.log("userFlags concernés", userFlagsConcerned)
                if (userFlagsConcerned) {
                    for (const userFlag of userFlagsConcerned) {
                        const notification: NotificationSend =  {
                            type: 'Signalement',
                            status: 'UNREAD',
                            description: `Un signalement concernant l'équipe ${selectedFlag.value?.team1.name} et l'équipe ${selectedFlag.value?.team2.name} a été refusé par ${currentUser.firstName} ${currentUser.lastName} avec la raison : ${comment.value}`,
                            emitterId: currentUser.id?currentUser.id:0,
                            receiverId: userFlag.user.id?userFlag.user.id:0,
                            date: new Date(),
                        }
                        let notifRes: any;
                        if (notification.emitterId != notification.receiverId) {
                            notifRes = await notificationService.createNotification(notification);
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
                        description: `Un signalement concernant l'équipe ${selectedFlag.value?.team1.name} et l'équipe ${selectedFlag.value?.team2.name} a été refusé par ${currentUser.firstName} ${currentUser.lastName} avec la raison : ${comment.value}`,
                        emitterId: currentUser.id?currentUser.id:0,
                        receiverId: teacher.id?teacher.id:0,
                        date: new Date(),
                        }
                        const notifRes = await notificationService.createNotification(notification);
                        if (notifRes) {
                        console.log("notification", notifRes);
                        }
                    }
                    }
                }
                
                emitVote('Le signalement a été supprimé');
            }
            return false;
        };

        const cancelVote = () => {
            toast.warn('Vote annulé');
        };

        const getIcon = (validated: boolean | null) => {
            if (validated === null) {
                return ErrorCircleFill;
            } else if (validated) {
                return CheckCircleFill;
            } else {
                return ErrorCircleFill;
            }
        };

        const getColor = (validated: boolean | null) => {
            if (validated === null) {
                return 'gray';
            } else if (validated) {
                return 'green';
            } else {
                return 'red';
            }
        };

        const getStatus = (validated: boolean | null) => {
            if (validated === null) {
                return 'En attente';
            } else if (validated) {
                return 'Validé';
            } else {
                return 'Refusé';
            }
        };

        const emitVote = (message: string) => {
            // Store the toast message in the localStorage
            localStorage.setItem('toastMessage', message);

            // Store the last flag in the localStorage
            localStorage.setItem('lastFlag', JSON.stringify(selectedFlag.value));

            // Reload the page
            window.location.reload();
        };

        /*const getLastClickedFlag = () => {
            return selectedFlag.value;
        };*/

        return {
            flags,
            handleClick,
            selectedFlag,
            userFlags,
            getOriginalTeam,
            getChangedTeam,
            inverted: ref(false),
            formatDate,
            validateFlag,
            cancelFlag,
            cancelVote,
            CheckCircleFill,
            ErrorCircleFill,
            SyncStatusSolid,
            getIcon,
            getColor,
            getStatus,
            loading,
            Loading,
            menuOptions,
            TimeOutline,
            ChatboxOutline,
            BodyOutline,
            PeopleOutline,
            sortedUserFlags,
            Vote24Regular,
            ArrowUndoOutline,
            ArrowRedoOutline,
            openDialog,
            PersonOutline,
            isFlagValidated,
            Empty,
            FlagOutline
        };
    },
});
</script>

<style scoped>
.vote-buttons{
    display: flex;
    gap: 10px;
    margin-top: 10px;
}
.vote {
    margin-top: 20px;
}
.name-container {
    display: flex;
    gap: 5px;
    align-items: center;
}
.text-colored {
    color: var(--primaryColor);
}
.text-colored-gap {
    color: var(--primaryColor);
    margin-bottom: 10px; 
}
.colored-italic {
    color: var(--primaryColor);
    font-style: italic;
}
.no-flags {
    color: var(--primaryColor);
    font-size: 1.5rem;
    font-weight: 500;
    margin-top: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
}
.container {
    display: flex;
    width: 100%;
    min-height: 100vh;
    padding: 0px;
}
.menu {
    flex-basis: 25%;
    background-color: var(--onBackground);
}
.display-container {
    display: flex;
    flex-basis: 75%;
    padding: 0px;
}
.left-container {
    flex: 1;
}
.right-container {
    flex: 1;
}

.icon-text {
    display: flex;
    align-items: center;
    padding: 5px;
    gap: 5px;
}

.breadcrumb {
  margin-left: 10px;
  margin-top: 70px;
}

</style>