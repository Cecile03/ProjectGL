<template>
    <Loading v-if="loading" />
    <Breadcrumb class="breadcrumb"/>
    <Empty v-if="flags.length == 0" 
        title="Aucun signalement"
        subtitle="Il n'y a aucun signalement à afficher"
        :icon="FlagOutline"
        path="/flag/flagInstanciate"
        bouton-string="Effectuer un signalement"
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
                    <div class="icon-text">
                        <n-icon :size="24" :component="Vote24Regular" />
                        <span>Voulez-vous valider ou refuser les changements émits par le signalement ? </span>
                    </div>
                    <div class="vote-buttons">
                        <n-popconfirm
                            @positive-click="validateFlag"
                            @negative-click="cancelVote"
                            negative-text="Annuler"
                            positive-text="Valider"
                            :negative-button-props="{ type: 'info' }"
                            :positive-button-props="{ type: 'default' }"
                        >
                            <template #trigger>
                            <n-button >Valider</n-button>
                            </template>
                            Pour que le signalement soit validé, tous les utilisateurs concernées doivent valider. Voulez vous confirmer ?
                        </n-popconfirm>
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
import { toast } from 'vue3-toastify';
import { type Flag, type UserFlag, type User, type NotificationSend } from '@/services/types';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useUserStore } from '@/stores/useUserStore';
import CheckCircleFill from '@vicons/fluent/CheckmarkCircle12Filled';
import ErrorCircleFill from '@vicons/fluent/ErrorCircle12Filled';
import SyncStatusSolid from '@vicons/fluent/AccessTime24Filled';
import Loading from '@/components/Loading.vue';
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
import { userService } from '@/services/user.service';

export default defineComponent({
    setup() {
        const flags = ref<Flag[]>([]);
        const selectedFlag = ref<Flag | null>(null);
        const userFlags = ref<UserFlag[]>([]);
        let currentUser: User = useUserStore().getUser() as User;
        const dialog = useDialog(); 
        let loading = ref(false);
        const comment = ref('');

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
                const flagsWithUserFlags = await Promise.all(result.map(async (flag) => {
                    const userFlags = await flagService.getUserFlagByFlagId(flag.id ?? 0);
                    console.log("userFlags", userFlags);
                    return { ...flag, userFlags };
                }));
                flags.value = flagsWithUserFlags.filter(flag => 
                    flag.userFlags?.some(userFlag => 
                        userFlag.flag.id === flag.id && 
                        (userFlag.flag.team1.users.some(user => user.id === currentUser.id) || 
                        userFlag.flag.team2.users.some(user => user.id === currentUser.id))
                    )
                );
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

        const handleClick = async (key: number) => {
            const flag: Flag | null = flags.value.find(flag => flag.id === key) || null;
            selectedFlag.value = flag;
            if (selectedFlag.value) {
                const result = await flagService.getUserFlagByFlagId(flag?.id ?? 0);
                if (result) {
                    userFlags.value = result;
                    console.log("userFlags", userFlags);
                }
            }
            else {
                //toast.error('Aucun flag detecté');
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

        const validateFlag = async () => {
            try {
                const userFlag = userFlags.value.find(uf => uf.flag.id === selectedFlag.value?.id && uf.user.id === currentUser.id);
                if (userFlag) {
                    const result = await flagService.setUserFlagValidated(userFlag.id ?? 0, true);
                    if (result) {
                        emitVote('Le signalement a été validé avec succès');
                        return true;
                    }
                } else {
                    toast.error('Aucun signalement correspondant trouvé');
                }
            } catch (error) {
                toast.error('Erreur lors de la validation du signalement');
            }
            return false;
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
                const userFlag = userFlags.value.find(uf => uf.flag.id === selectedFlag.value?.id && uf.user.id === currentUser.id);
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
            localStorage.setItem('lastFlag', JSON.stringify(getLastClickedFlag()));

            // Reload the page
            window.location.reload();
        };

        const getLastClickedFlag = () => {
            return selectedFlag.value;
        };

        return {
            flags,
            handleClick,
            selectedFlag,
            userFlags,
            getOriginalTeam,
            getChangedTeam,
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
            inverted: ref(false),
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