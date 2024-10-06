// SubGradeService.ts
import { AxiosClient } from '@/services/index';
import type { Evaluation } from '@/services/types';

const API_URL = '/subgrade';

const updateSubGrade = async (userId: number, sprintId: number, gradeType: string) => {
  try {
    const response = await AxiosClient.put(`${API_URL}/update`, { userId, sprintId, gradeType });
    return response.data;
  } catch (error) {
    console.error('Failed to update subgrade:', error);
    throw error;
  }
}

const getSubGradeByUserIdAndSprintIdAndGradeType = async (userId: number, sprintId: number, gradeType: string) => {
  try {
    const response = await AxiosClient.get(`${API_URL}`, { params: { userId, sprintId, gradeType } });
    return response.data;
  } catch (error) {
    console.error('Failed to get subgrade:', error);
    throw error;
  }
}

const getEvaluationBySubgrade = async (userId: number, sprintId: number, gradeType: string, evaluatorId: number) : Promise<Evaluation> => {
  try {
    const response = await AxiosClient.get(`${API_URL}/evaluation`, { params: { userId, sprintId, gradeType, evaluatorId } });
    return response.data;
  } catch (error) {
    console.error('Failed to get evaluation by subgrade:', error);
    throw error;
  }
}

export const subGradeService = {
  updateSubGrade,
  getSubGradeByUserIdAndSprintIdAndGradeType,
  getEvaluationBySubgrade
};