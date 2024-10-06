import { AxiosClient } from '@/services/index';

export type Sprint = {
  id: number;
  startDate: Date;
  endDate: Date;
  endType: string;
};

// Service to create a sprint
const createSprint = async (sprintDTO: Sprint): Promise<Sprint | null> => {
  try {
    const response = await AxiosClient.post('/sprint', sprintDTO);
    if (response.data) {
      return {
        id: response.data.id,
        startDate: new Date(response.data.startDate),
        endDate: new Date(response.data.endDate),
        endType: response.data.endType,
      };
    }
    return null;
  } catch (error) {
    console.error('Error creating sprint:', error);
    return null;
  }
};

// Service to update a sprint
const updateSprint = async (sprintDTO: Sprint): Promise<Sprint | null> => {
  try {
    const response = await AxiosClient.put(`/sprint`, sprintDTO);
    if (response.data) {
      return {
        id: response.data.id,
        startDate: new Date(response.data.startDate),
        endDate: new Date(response.data.endDate),
        endType: response.data.endType,
      };
    }
    return null;
  } catch (error) {
    console.error('Error updating sprint:', error);
    return null;
  }
};

const getAllSprints = async (): Promise<Array<Sprint> | null> => {
  try {
    const response = await AxiosClient.get('/sprint');

    if (response.data.length) {
      return response.data.map((sprint: any) => ({
        id: sprint.id,
        startDate: new Date(sprint.startDate),
        endDate: new Date(sprint.endDate),
        endType: sprint.endType,
      }));
    }

    return null;
  } catch (error) {
    console.error('Error fetching sprints:', error);
    return null;
  }
};

// Service to delete all sprints
const deleteAll = async (): Promise<void> => {
  try {
    await AxiosClient.delete('/sprint');
  } catch (error) {
    console.error('Error deleting all sprints:', error);
  }
};

const getSprintById = async (id: number): Promise<Sprint | null> => {
  try {
    const response = await AxiosClient.get(`/sprint/${id}`);
    if (response.data) {
      return {
        id: response.data.id,
        startDate: new Date(response.data.startDate),
        endDate: new Date(response.data.endDate),
        endType: response.data.endType,
      };
    }
    return null;
  } catch (error) {
    console.error('Error fetching sprint by id:', error);
    return null;
  }
};

export const sprintService = {
  createSprint,
  updateSprint,
  getAllSprints,
  deleteAll,
  getSprintById,
};
