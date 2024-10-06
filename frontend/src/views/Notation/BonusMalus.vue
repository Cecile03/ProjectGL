<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { bonusMalusService } from '@/services/bonusMalus.service';
import { toast } from 'vue3-toastify';
import { teamService } from '@/services/team.service';
import type { Team } from '@/services/types';
import { useUserStore } from '@/stores/useUserStore';
import Loading from '@/components/Loading.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';
import { useDialog } from 'naive-ui';
import SprintSelector from '@/components/SprintSelector.vue';
import {
  AddSubtractCircle24Filled,
  ArrowCircleLeft48Regular,
} from '@vicons/fluent';
import Empty from '@/components/Empty.vue';
import { PeopleOutline } from '@vicons/ionicons5';

export type BonusMalusStatus = 'PENDING' | 'VALIDATED' | 'REJECTED';

export type BonusMalus = {
  id: number;
  value: number;
  comment: string;
  status: BonusMalusStatus;
  isUnlimited: boolean;
  attributedTo: number;
  attributedBy: number;
  teamId: number;
  sprintId: number;
};

type FormData = {
  memberId?: number;
  value: number;
  comment: string;
};

const selectedSprintId = computed(() => useUserStore().getSelectedSprintId());
const user = useUserStore().getUser();
const isOS = user?.roles.includes('OS');
const dialog = useDialog();

const loading = ref(false);
const loadingRefresh = ref(false);
const loadingAddBM = ref(false);
const loadingValidateBM = ref(false);
const userTeam = ref<Team | undefined>(undefined);
const bonusMalus = ref<BonusMalus[]>([]);
const formValues = ref<FormData[]>([]);
const OSWantsChange = ref(false);
const membersWhoValidateBM = ref<number[]>([]);
const isAllMembersValidateBM = ref(false);
const SSWantsSeeLBM = ref(false);

const fetchUserTeam = async () => {
  const teams = await teamService.getAll();
  if (teams) {
    if (isOS) {
      // while waiting teamId is stored in User
      userTeam.value = teams.find(
        (t) => t.users.find((u) => u.id == user?.id)?.id == user?.id
      );
    } else {
      userTeam.value = teams.find((t) => t.supervisor.id == user?.id);
    }
  }
};

const fetchBonusMalus = async () => {
  try {
    const res = await bonusMalusService.getTeamBM(
      isOS || SSWantsSeeLBM.value ? false : true, // if user is student or an SS who wants see LBM, fetch limited B/M, else unlimited B/M
      userTeam.value?.id ?? 0,
      selectedSprintId.value ?? 0
    );

    if (res) {
      bonusMalus.value = res;
    } else {
      bonusMalus.value = [];
    }
    loadFormValues();
  } catch (error) {
    console.error(error);
    toast.error('Une erreur est survenue.');
  }
};

const fetchMembersWhoValidateBM = async () => {
  const idList = await bonusMalusService.getMembersWhoValidateBM(
    userTeam.value?.id ?? 0,
    selectedSprintId.value ?? 0
  );
  if (idList) {
    membersWhoValidateBM.value = idList;
    isAllMembersValidateBM.value =
      idList.length == userTeam.value?.users.length;
  } else {
    membersWhoValidateBM.value = [];
    isAllMembersValidateBM.value = false;
  }
};

onMounted(async () => {
  loading.value = true;
  await fetchUserTeam();
  await fetchBonusMalus();
  await fetchMembersWhoValidateBM();
  loading.value = false;
});

const loadFormValues = () => {
  const teamUsers = userTeam.value?.users;
  if (teamUsers) {
    formValues.value = teamUsers.map((user) => ({
      memberId: user.id ?? 0,
      value:
        bonusMalus.value.find((bm) => bm.attributedTo === user.id)?.value ?? 0,
      comment:
        bonusMalus.value.find((bm) => bm.attributedTo === user.id)?.comment ??
        '',
    }));
  }
};

watch(selectedSprintId, async () => {
  loadingRefresh.value = true;
  await fetchBonusMalus();
  await fetchMembersWhoValidateBM();
  loadingRefresh.value = false;
});
watch(SSWantsSeeLBM, () => fetchBonusMalus());

const validateForm = () => {
  const invalidEntries = formValues.value.filter(
    (entry) =>
      (entry.value !== 0 && entry.comment === '') ||
      (entry.comment !== '' && entry.value === 0)
  );

  if (invalidEntries.length > 0) {
    // Il y a des entrées invalides
    toast.error('Des bonus/malus ne sont pas entièrement complétés.');
    return false;
  } else {
    // Toutes les entrées sont valides
    return true;
  }
};

const validateEquality = () => {
  let totalVal = 0;
  formValues.value.forEach((item) => (totalVal += item.value));
  return totalVal === 0;
};
const handleAddBM = () => {
  if (userTeam.value && validateForm()) {
    const bonusMalusList = formValues.value.map((entry) => ({
      attributedBy: user?.id ?? 0,
      attributedTo: entry.memberId,
      comment: entry.comment,
      value: entry.value,
    }));
    loadingAddBM.value = true;
    bonusMalusService
      .addBonusMalus(
        userTeam.value.id,
        bonusMalusList,
        selectedSprintId.value ?? 0
      )
      .then(() => toast.success('Bonus/Malus enregistrés.'))
      .catch(() => toast.error("erreur lors de l'ajout."))
      .finally(() => {
        loadingAddBM.value = false;
        OSWantsChange.value = false;
      });
  }
};

const handleOSAddBm = () => {
  dialog.warning({
    title: ' Bonus / Malus',
    content:
      'En validant vos changements, les précédents bonus et malus en cours de validation seront supprimés. Concertez votre équipe afin de faciliter la procédure de validation.',
    positiveText: 'Je remplace les B/M',
    negativeText: 'Annuler',
    onPositiveClick: () => {
      if (validateEquality()) {
        handleAddBM();
      } else {
        toast.warning('La sommes des B/M doit être égale à 0');
      }
    },
  });
};

const handleValidateLBM = () => {
  loadingValidateBM.value = true;
  bonusMalusService
    .validateBonusLMalus(userTeam.value?.id ?? 0, selectedSprintId.value ?? 0)
    .then(() => {
      if (user?.id) membersWhoValidateBM.value.push(user?.id);
      toast.success('Bonus et malus validés');
      SSWantsSeeLBM.value = false;
    })
    .finally(() => (loadingValidateBM.value = false));
};
</script>
<template>
  <div class="breadcrumb-abs"><Breadcrumb /></div>
  <h1 class="title">
    Bonus et malus {{ SSWantsSeeLBM || isOS ? 'limités' : 'ilimités' }} de mon
    équipe
  </h1>
  <Loading v-if="loading" />
  <Empty
    v-else-if="!userTeam"
    title="Aucune équipe"
    subtitle="Il n'y a aucune équipe à afficher"
    :icon="PeopleOutline"
    path="/notation"
    bouton-string="Retour" />

  <div v-else class="bonusMalus">
    <div class="left-side">
      <div class="teamTable">
        <n-table :bordered="false" :single-line="false">
          <thead>
            <tr>
              <th>Nom prénom</th>
              <th>Bonus / Malus</th>
              <th>Commentaire</th>
            </tr>
          </thead>
          <!-- This is the view for an SS, he is able to directly modify B/M. -->
          <tbody v-if="(!isOS && !SSWantsSeeLBM) || OSWantsChange">
            <tr v-for="(member, index) in userTeam?.users" :key="member.id">
              <td class="name">
                {{ `${member.lastName} ${member.firstName}` }}
              </td>
              <td class="value">
                <n-input-number
                  class="bm"
                  v-model:value="formValues[index].value"
                  min="-20"
                  max="20"
                  clearable />
              </td>
              <td class="comment">
                <n-input
                  v-model:value="formValues[index].comment"
                  type="text"
                  placeholder="Commentaire" />
              </td>
            </tr>
          </tbody>
          <!-- This is the view for an OS, after warning, he is able to suggest another B/M list. -->
          <!-- He is also able to see who in the team validate B/M list. -->
          <tbody v-else>
            <tr v-for="(member, index) in userTeam?.users" :key="member.id">
              <td class="name">
                <div class="validationStatus">
                  {{ `${member.lastName} ${member.firstName}` }}
                  <!-- Display validate icon for the member who valide B/M list -->
                  <n-tooltip
                    trigger="hover"
                    v-if="membersWhoValidateBM.find((id) => id == member.id)">
                    <template #trigger>
                      <i class="fa-solid fa-circle-check"></i>
                    </template>
                    À validé(e) les bonus et malus attribués.
                  </n-tooltip>
                </div>
              </td>
              <td class="value">
                {{ formValues[index].value }}
              </td>
              <td class="comment">
                {{ formValues[index].comment }}
              </td>
            </tr>
          </tbody>
        </n-table>
      </div>
    </div>

    <div class="right-side">
      <SprintSelector size="medium" />
      <!-- View to indicate to an SS that all members of his team validate limited B/M -->
      <div
        v-if="isAllMembersValidateBM && !isOS && !SSWantsSeeLBM"
        class="isAllMemberValidate">
        <n-icon
          class="icon"
          :size="32"
          :component="AddSubtractCircle24Filled"></n-icon>
        <p class="msg">
          Les bonus et malus limités ont été distribués et validés par les
          étudiants pour ce sprint.
        </p>
        <p @click="SSWantsSeeLBM = true" class="seeLBM-btn">
          <n-icon :size="24" :component="ArrowCircleLeft48Regular"></n-icon>
          Voir les bonus et malus limités attribués
        </p>
      </div>
      <!-- Buttons view for OS -->
      <div v-if="isOS" class="button-container">
        <p
          v-if="membersWhoValidateBM.find((id) => id == user?.id)"
          class="bm-validated">
          Vous avez validé la liste de bonus et malus pour votre équipe.
        </p>
        <div v-else-if="!OSWantsChange" class="button-container">
          <n-button type="warning" @click="OSWantsChange = true"
            >Proposer de nouveaux B/M</n-button
          >
          <n-button @click="handleValidateLBM" :loading="loadingValidateBM"
            >Valider les B/M attribués</n-button
          >
        </div>
        <div v-else class="button-container">
          <n-button @click="OSWantsChange = false" type="secondary"
            >Annuler</n-button
          >
          <n-button @click="handleOSAddBm" :loading="loadingAddBM"
            >Valider</n-button
          >
        </div>
      </div>
      <!-- Buttons view for SS -->
      <div v-else class="button-container">
        <div v-if="SSWantsSeeLBM" class="button-container">
          <n-button @click="SSWantsSeeLBM = false" type="secondary"
            >Annuler</n-button
          >
          <n-button
            v-if="SSWantsSeeLBM"
            @click="handleValidateLBM"
            :loading="loadingValidateBM"
            >Je valide les B/M limités</n-button
          >
        </div>

        <n-button v-else @click="handleAddBM" :loading="loadingAddBM"
          >Enregistrer</n-button
        >
      </div>
    </div>
  </div>
</template>

<style scoped>
.breadcrumb-abs {
  position: absolute;
  top: -3px;
  left: 50px;
}
.bonusMalus {
  padding: 30px;
  display: flex;
  width: 100%;
}
.title {
  text-align: center;
  font-size: 1.5rem;
  color: var(--textColor);
  margin-top: 80px;
  margin-bottom: 10px;
}
.left-side {
  flex: 3;
  display: flex;
  justify-content: center;
  margin: 0 30px;
}
.right-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}
.teamTable {
  width: 100%;
  box-shadow: 2px 2px 10px 0 #0000001c;
  border-radius: 5px;
  height: 600px;
  overflow: scroll;
}
.name {
  width: 250px;
}
.bm {
  width: 150px;
}
.comment {
  width: 600px;
}
.button-container {
  display: flex;
  justify-content: center;
  margin: 0 auto;
  margin-top: 20px;
  gap: 20px;
}
.validationStatus {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.validationStatus i {
  color: var(--greenColor);
}

.isAllMemberValidate {
  display: flex;
  flex-direction: column;
}
.isAllMemberValidate .icon {
  color: var(--greenColor);
  margin: 0 auto;
}
.isAllMemberValidate .msg {
  text-align: center;
}
.isAllMemberValidate .seeLBM-btn {
  margin: 30px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--primaryColor);
  text-decoration: underline;
  cursor: pointer;
  transition: 0.3s;
}
.isAllMemberValidate .seeLBM-btn:hover {
  color: var(--secondaryColor);
}
</style>
