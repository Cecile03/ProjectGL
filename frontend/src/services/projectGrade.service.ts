import { AxiosClient } from '@/services/index';
import type { ProjectGrade } from '@/services/types';

const API_URL = '/projectGrade'; // replace with your API URL

const saveProjectGrade= async (userId: number, sprintId: number): Promise<boolean> => {
  try {
    const response = await AxiosClient.post(`${API_URL}/${userId}/${sprintId}`);
    return response.status === 200;
  } catch (error) {
    console.error('Error saving project grade:', error);
    throw error;
  }
}

const updateProjectGrade = async (userId: number, sprintId: number): Promise<ProjectGrade> => {
  try {
    const response = await AxiosClient.put(`${API_URL}/${userId}/${sprintId}`);
    return response.data;
  } catch (error) {
    console.error('Error updating project grade:', error);
    throw error;
  }
}

const getProjectGradeByUserIdAndSprintId = async(userId: number, sprintId: number): Promise<ProjectGrade> => {
  try {
    const response = await AxiosClient.get(`${API_URL}/${userId}/${sprintId}`);
    return response.data;
  } catch (error) {
    console.error('Error getting project grade:', error);
    throw error;
  }
}

const getAll = async(): Promise<any> => {
  try {
    const response = await AxiosClient.get(`${API_URL}/all`);
    return response.data;
  } catch (error) {
    console.error('Error getting project grade:', error);
    throw error;
  }
}

const validateProjectGrade = async (projectGradeId: number): Promise<boolean> => {
  try {
    const response = await AxiosClient.put(`${API_URL}/validate/${projectGradeId}`);
    return response.status===200;
  } catch (error) {
    console.error('Error updating project grade:', error);
    return false;
  }
}

export const projectGradeService = {
  saveProjectGrade,
  updateProjectGrade,
  getProjectGradeByUserIdAndSprintId,
  getAll,
  validateProjectGrade
};