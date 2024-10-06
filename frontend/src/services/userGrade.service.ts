import { AxiosClient } from '@/services/index';

const API_URL = '/userGrade';

const updateUserGrade = async (userId: number, sprintId: number, evaluatorId: number, grade: number) => {
  try {
    const response = await AxiosClient.put(`${API_URL}/${userId}/${sprintId}/${evaluatorId}/${grade}`);
    return response.status === 200;
  } catch (error) {
    console.error('Error saving UserGrade:', error);
    return false;
  }
};

const getUserGrades = async (userId: number, sprintId: number) => {
  try {
    const response = await AxiosClient.get(`${API_URL}/list`, {
      params: {
        userId: userId,
        sprintId: sprintId,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error getting UserGrades:', error);
    return null;
  }
};

const getUserGrade = async (userId: number, sprintId: number, evaluatorId: number) => {
  try {
    const response = await AxiosClient.get(API_URL, {
      params: {
        userId: userId,
        sprintId: sprintId,
        evaluatorId: evaluatorId,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error getting UserGrade:', error);
    return null;
  }
};

export const userGradeService = {
  updateUserGrade,
  getUserGrades,
  getUserGrade,
};