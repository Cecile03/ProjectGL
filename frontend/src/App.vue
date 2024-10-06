<script setup lang="ts">
import { useRoute } from 'vue-router';
import Navbar from './components/Navbar.vue';
import { NDialogProvider, type GlobalThemeOverrides } from 'naive-ui';
import { onBeforeMount, ref } from 'vue';
import { useUserStore } from './stores/useUserStore';
import Loading from './components/Loading.vue';

const route = useRoute();
const userStore = useUserStore();

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#0087e0',
    primaryColorHover: '#76bcea',
    fontFamily: 'Roboto, sans-serif',
  },
  Button: {
    peers: {
      Dialog: {
        iconColorSuccess: '#0087e0',
      },
    },
    borderRadiusMedium: '5px',
    borderRadiusSmall: '5px',
    //type : 'default'
    color: '#0087e0',
    textColor: '#fff',
    colorHover: '#76bcea',
    textColorHover: '#fff',
    border: '2px solid #0087e0',
    borderHover: '#fff',
    //quand on clique
    textColorPressed: '#fff',
    //après avoir été cliqué
    textColorFocus: '#8c8c8c',
    borderFocus: '2px solid #8c8c8c',
    //type : 'info'
    colorInfo: '#fff',
    textColorInfo: '#0087e0',
    textColorHoverInfo: '#fff',
    colorOpacitySecondary: '0.01',
    borderInfo: '2px solid #0087e0',
    borderHoverInfo: '',
    colorHoverInfo: '#76bcea',
    //après avoir cliqué
    colorFocusInfo: '#fff',
    textColorFocusInfo: '#b2b2b2',
    borderFocusInfo: '2px solid #b2b2b2',
  },
  Dropdown: {
    optionTextColor: '#1b1336',
    optionTextColorHover: '#0087e0',
    optionTextColorActive: '#0087e0',
    color: '#fff',
    colorHover: '#8c8c8c',
  },
  Dialog: {
    color: '#fff',
    iconColorSuccess: '#0087e0',
  },
  DataTable: {
    thTextColor: '#1b1336',
    tdTextColor: '#1b1336',
    thColor: '#ededed',
    tdColorHover: '#b8dcf4',
    borderColor: '#e0e0e0',
  },
};
const loadinUser = ref(true);
onBeforeMount(() => {
  console.log('App.vue loadUser');
  userStore.loadUser().finally(() => (loadinUser.value = false));
});
</script>

<template>
  <Navbar v-if="route.path !== '/login' && userStore.getUser()" />
  <Loading v-if="loadinUser" />
  <div class="content" v-else>
    <n-config-provider :theme-overrides="themeOverrides">
      <n-dialog-provider>
        <n-message-provider>
          <RouterView />
        </n-message-provider>
      </n-dialog-provider>
    </n-config-provider>
  </div>
</template>

<style>
.content {
  margin-top: 65px;
  /* padding: 30px; */
  position: relative;
  max-width: 1600px;
  background-color: var(--backgroundColor);
}

@media screen and (max-width: 768px) {
  .content {
    padding: 20px;
    max-width: 1600px;
    margin: 0 auto;
  }
}
</style>
