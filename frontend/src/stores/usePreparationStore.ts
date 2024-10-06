import type { Team, User } from '@/services/types';
import { defineStore } from 'pinia';

// Following types could be moved to other files, it's a temporary declaration

export type TeamsDTO = {
  students: User[];
  teachers: User[];
  names: string[];
  status: 'none' | 'publish' | 'prepublish';
  numberOfTeams: number;
  numberOfGirlsPerTeam: number;
};
type Sprint = {
  name?: string;
  startDate: Date;
  endDate: Date;
};
export const usePreparationStore = defineStore({
  id: 'preparation',
  state: () => ({
    students: [] as User[],
    teams: [] as Team[],
    sprints: [] as Sprint[],
  }),
  getters: {
    getStudents: (state) => () => {
      return state.students;
    },
    getTeams: (state) => () => {
      return state.teams;
    },
  },
  actions: {
    addStudentList(students: User[]) {
      this.students = students;
    },
    addTeams(teams: Team[]) {
      this.teams = teams;
    },
  },
});
