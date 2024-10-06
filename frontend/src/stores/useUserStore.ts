import { authService } from '@/services/auth.service';
import type { User } from '@/services/types';
import { defineStore } from 'pinia';
import router from '@/router';

export const useUserStore = defineStore({
  id: 'user',
  state: () => ({
    user: null as User | null,
    isLoadingUser: false,
    selectedSprintId: null as number | null,
  }),
  getters: {
    getUser: (state) => () => {
      return state.user;
    },
    isAuthenticated: () => () => {
      const token = localStorage.getItem('token');
      if (token) return true;
      else return false;
    },
    getSelectedSprintId: (state) => () => {
      return state.selectedSprintId;
    },
  },
  actions: {
    logout() {
      localStorage.removeItem('token');
      router.push('/login');
    },
    async loadUser() {
      this.isLoadingUser = true;
      try {
        const wasAuthenticated = localStorage.getItem('token'); // check if user was authenticated the last time he visited the app
        if (!wasAuthenticated) return; // avoid 401

        const user = await authService.getMe();
        if (user) {
          console.log('loaded user: ', user);

          this.user = user;
        } else {
          // in case backend is not connected to send 401 err catched by interceptors
          this.logout();
        }
      } catch (err) {
        console.error('Error while loading user', err);
      } finally {
        this.isLoadingUser = false;
      }
    },
    setSelectedSprintId(id: number) {
      this.selectedSprintId = id;
    },
  },
});
