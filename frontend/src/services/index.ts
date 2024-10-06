import axios, { AxiosError } from 'axios';
import { CONFIG } from '@/utils/config';
import { toast } from 'vue3-toastify';
import { useUserStore } from '@/stores/useUserStore';

export const AxiosClient = axios.create({ baseURL: CONFIG.apiUrl });

AxiosClient.interceptors.request.use(function (config) {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

AxiosClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error: AxiosError) => {
    if (error.response) {
      const { status } = error.response;

      if (status === 403) {
        toast.warning("Vous n'êtes pas autorisé à faire cela");
      }

      if (status === 401) {
        useUserStore().logout();
      }
    }
    return Promise.reject(error);
  }
);
