import { AxiosClient } from './index';
import type { TeamsDTO } from '@/stores/usePreparationStore';
import type { Team } from './types';
import { formatUserResponse, type UserResponse } from './auth.service';
import { usePreparationStore } from '@/stores/usePreparationStore';
import type { UnwrapRef } from 'vue';

type TeamResponse = {
  id: number;
  name: string;
  status: 'none' | 'publish' | 'prepublish';
  supervisor: UserResponse;
  users: UserResponse[];
  validated: boolean;
  criteria: {
    id: number;
    minAverageThreshold: number;
    numberOfBachelor: number;
    numberOfGirls: number;
    numberOfTeams: number;
  };
};

const formatTeamsResponse = (req: TeamResponse[]): Team[] => {
  const teams: Team[] = req.map((team) => ({
    id: team.id,
    name: team.name,
    status: team.status,
    validated: team.validated,
    criteria: {
      id: team.criteria.id,
      minAverageThreshold: team.criteria.minAverageThreshold,
      numberOfBachelor: team.criteria.numberOfBachelor,
      numberOfGirls: team.criteria.numberOfGirls,
      numberOfTeams: team.criteria.numberOfTeams,
    },
    supervisor: formatUserResponse(team.supervisor),
    users: team.users.map((u) => formatUserResponse(u)),
  }));
  return teams;
};
const createTeams = async (teamsDTO: TeamsDTO) => {
  const response = await AxiosClient.post('/teams', teamsDTO);
  console.log('createTeams res: ', response);
  if (!response.data.length) return null;

  const teamsData = formatTeamsResponse(response.data);
  return teamsData;
};

const getAll = async (): Promise<Team[] | null> => {
  const res = await AxiosClient.get<TeamResponse[]>('/teams');
  console.log('team service getAll: ', res.data);

  if (!res.data.length) return null;
  return formatTeamsResponse(res.data);
};

const deleteAll = () => {
  return AxiosClient.delete('/teams');
};

const saveTeam = async (team: Team) => {
  console.log('Team à sauvegarder', team);
  try {
    const request = await AxiosClient.put('/teams', team);
    return request.data;
  } catch (error) {
    console.error(error);
    throw error;
  }
};

const getStudentList = async () => {
  return usePreparationStore().getStudents();
};

const getTeamById = async (id: number): Promise<Team> => {
  try {
    const response = await AxiosClient.get<Team>(`/teams/${id}`);
    return response.data;
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const teamService = {
  createTeams,
  getAll,
  deleteAll,
  saveTeam,
  getStudentList,
  getTeamById,
};


