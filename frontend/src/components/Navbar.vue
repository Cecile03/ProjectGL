<script lang="ts" setup>
import { h, type Component, ref, onMounted } from 'vue';
import canopusBan from '../assets/img/canopusBan.png';
import IconButton from './IconButton.vue';
import NavLink from './NavLink.vue';
import { useRouter } from 'vue-router';
import { NIcon } from 'naive-ui';
import {
  PersonCircleOutline as UserIcon,
  LogOutOutline as LogoutIcon,
  FlagOutline as Flag,
  NotificationsOutline as Notif,
  PersonOutline as Profile,
} from '@vicons/ionicons5';
import { useUserStore } from '@/stores/useUserStore';
import { flagService } from '@/services/flag.service';
import { notificationService } from '@/services/notification.service';

const router = useRouter();
const userStore = useUserStore();
const currentUser = userStore.getUser();
const userFlagsCount = ref(0);
const userNotifsCount = ref(0);

onMounted(async () => {
  const flags = await flagService.getFlags();
  if (flags) {
    let count = 0;
    for (const flag of flags) {
      if (flag.id != undefined) {
        const userFlags = await flagService.getUserFlagByFlagId(flag.id);
        if (userFlags) {
          count += userFlags.filter(
            (userFlag) => currentUser && userFlag.user.id === currentUser.id
          ).length;
        }
      }
    }
    userFlagsCount.value = count;
    if (
      currentUser?.roles.includes('SS') ||
      currentUser?.roles.includes('PL')
    ) {
      userFlagsCount.value = flags.length;
    }
  }
  const notifs = await notificationService.getAllByUserId(currentUser?.id ?? 0);
  if (notifs) {
    notifs.forEach((notif) => {
      if (notif.status === 'UNREAD') userNotifsCount.value++;
    });
  }
});

const renderIcon = (icon: Component) => {
  return () => {
    return h(NIcon, null, {
      default: () => h(icon),
    });
  };
};

const profileOptions = [
  {
    label: 'Mon profil',
    key: 'profile',
    icon: renderIcon(UserIcon),
  },
  {
    label: 'Se déconnecter',
    key: 'logout',
    icon: renderIcon(LogoutIcon),
  },
];

const handleSelectProfile = (key: string | number) => {
  if (key === 'profile') router.push('/profile');
  if (key === 'logout') userStore.logout();
};
</script>

<template>
  <div class="navbar">
    <div class="nav-left-side">
      <img v-bind:src="canopusBan" class="nav-logo" @click="router.push('/')" />
      <div class="nav-links">
        <NavLink redirect-to="/notation" name="Notations" />
        <NavLink redirect-to="/teams" name="Equipes" />
        <NavLink
          redirect-to="/preparation"
          name="Préparation"
          v-if="
            userStore.getUser()?.roles.includes('OL') ||
            userStore.getUser()?.roles.includes('PL')
          " />
      </div>
    </div>
    <div class="nav-right-side">
      <div class="nav-shortcuts">
        <IconButton redirect-to="/flag" :icon="Flag" :badge="userFlagsCount" />
        <IconButton
          redirect-to="/notification"
          :icon="Notif"
          :badge="userNotifsCount" />
        <n-dropdown :options="profileOptions" @select="handleSelectProfile">
          <IconButton
            redirect-to="/profile"
            :icon="Profile"
            :is-profile="true" />
        </n-dropdown>
      </div>
    </div>
  </div>
</template>

<style>
.navbar {
  display: flex;
  justify-content: space-between;
  padding: 12px 30px;
  position: fixed;
  z-index: 1000;
  top: 0;
  left: 0;
  right: 0;
  background-color: white;
  border-bottom: 1px solid #ece6e6;
}
.nav-left-side,
.nav-right-side {
  display: flex;
  align-items: center;
}
.nav-left-side {
  gap: 70px;
}
.nav-logo {
  width: 120px;
  cursor: pointer;
}
.nav-links {
  display: flex;
  gap: 30px;
}
.nav-shortcuts {
  display: flex;
}
</style>
