import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import LogIn from '@/views/Auth/LogIn.vue';
import Preparation from '@/views/Preparation/Preparation.vue';
import UploadFile from '@/views/Preparation/components/UploadFile.vue';
import Sprint from '@/views/Preparation/components/Sprint.vue';
import GradeScale from '@/views/Preparation/components/GradeScale.vue';
import Preview from '@/views/Preparation/components/Preview.vue';
import PageUpload from '@/views/Preparation/components/PageUpload.vue';
import Notification from '@/views/Notification/Notification.vue';
import { useUserStore } from '@/stores/useUserStore';
import Profile from '@/views/Profile/Profile.vue';
import Teams from '@/views/Teams/Teams.vue';
import TeamsConfig from '@/views/Preparation/components/TeamsConfig.vue';
import Flag from '@/views/Flag/Flag.vue';
import FlagInstanciate from '@/views/Flag/components/FlagInstanciate.vue';
import FlagView from '@/views/Flag/components/FlagView.vue';
import FlagViewPL from '@/views/Flag/components/FlagViewPL.vue';
import Notation from '@/views/Notation/Notation.vue';
import BonusMalus from '@/views/Notation/BonusMalus.vue';
import NotationProjectProf from '@/views/Notation/NotationProjectProf.vue';
import IndividualGrading from '@/views/Notation/Oral/IndividualGrading.vue';
import TeamGrading from '@/views/Notation/Oral/TeamGrading.vue';
import RunningOrder from '@/views/Notation/Oral/RunningOrder.vue';
import type { UserRole } from '@/services/types';
import { watch } from 'vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // redirect to /home if url doesn't exist
    { path: '/:pathMatch(.*)*', redirect: '/' },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      component: LogIn,
    },
    {
      path: '/notation',
      component: Notation,
      name: 'Notation',
      children: [
        {
          path: '',
          name: 'Notation',
          component: Notation,
        },
        {
          path: 'project',
          name: 'Projet',
          component: NotationProjectProf,
          meta: { roles: ['SS', 'TC'] },
        },
        {
          path: 'bm',
          name: 'BonusMalus',
          component: BonusMalus,
          meta: { roles: ['SS', 'OS'] },
        },
        {
          path: 'oral',
          name: 'Oral',
          redirect: '/notation',
          children: [
            {
              path: 'indiv',
              name: 'Individuelle',
              component: IndividualGrading,
              meta: { roles: ['SS', 'TC', 'OL', 'PL'] },
            },
            {
              path: 'teams',
              name: 'Equipes',
              component: TeamGrading,
            },
            {
              path: 'runningOrder',
              name: 'Ordre de passage',
              component: RunningOrder,
            },
          ],
        },
      ],
    },
    {
      path: '/teams',
      name: 'Les équipes',
      component: Teams,
    },
    {
      path: '/preparation',
      name: 'Préparation',
      component: Preparation,
      meta: { roles: ['OL', 'PL'] },
      children: [
        {
          path: 'create',
          name: 'Création liste',
          component: UploadFile,
        },
        {
          path: 'import',
          name: 'Importer étudiants',
          component: PageUpload,
        },
        {
          path: 'config',
          name: 'Configuration équipes',
          component: TeamsConfig,
        },
        {
          path: 'preview',
          name: 'Visualisation',
          component: Preview,
        },
        {
          path: 'sprint',
          name: 'Sprint',
          component: Sprint,
        },
        {
          path: 'gradescale',
          name: 'Echelle de notes',
          component: GradeScale,
        },
      ],
    },
    {
      path: '/flag',
      name: 'Signalement',
      component: Flag,
      children: [
        {
          path: '/flag',
          name: 'Signalement',
          component: Flag,
        },
        {
          path: 'flagInstanciate',
          name: 'Effectuer un signalement',
          component: FlagInstanciate,
          meta: { roles: ['SS', 'OS'] },
        },
        {
          path: 'flagView',
          name: 'Affichage des signalements',
          component: FlagView,
          meta: { roles: ['OS'] },
        },
        {
          path: 'flagViewPL',
          name: 'Action des signalements',
          component: FlagViewPL,
          meta: { roles: ['SS', 'PL'] },
        },
      ],
    },
    {
      path: '/notification',
      name: 'Notification',
      component: Notification,
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  // Wait User is loaded if page was refreshed
  if (userStore.isLoadingUser) {
    await new Promise<void>((resolve) => {
      const unwatch = watch(
        () => userStore.isLoadingUser,
        (newVal) => {
          if (!newVal) {
            resolve();
            unwatch(); // Stop watching when user is loaded
          }
        }
      );
    });
  }

  const isAuthenticated = userStore.isAuthenticated();
  const userRoles = userStore.getUser()?.roles;

  if (to.path !== '/login' && !isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && isAuthenticated) {
    next('/');
  } else if (to.meta.roles) {
    if (
      Array.isArray(userRoles) &&
      userRoles.some((role) => (to.meta.roles as UserRole[]).includes(role))
    ) {
      next();
    } else {
      next('/');
    }
  } else {
    next();
  }
});

export default router;
