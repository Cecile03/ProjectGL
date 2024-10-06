import { AxiosClient } from './index';
import type { Feedback } from '@/services/types';

const createFeedbacks = async (feedback: Feedback) => {
  try {
    const response = await AxiosClient.put('/feedbacks', feedback);
    return response.status === 200;
  } catch (error) {
    console.error('Error creating feedbacks:', error);
    return false;
  }
};

const getFeedbacksByTeamAndSprint = async (
  teamId: number,
  sprintId: number
): Promise<Feedback[] | null> => {
  const res = await AxiosClient.get<Feedback[]>('/feedbacks', {
    params: {
      teamId: teamId,
      sprintId: sprintId,
    },
  });
  console.log('teamId', teamId)
  console.log('sprintId', sprintId)
  console.log('res', res);
  if (!res.data.length) return null;
  return res.data;
};

const deleteFeedback = async (feedback: Feedback) => {
  try {
    const reponse = await AxiosClient.delete('/feedbacks', {
      data: feedback,
    });
    return reponse.status === 200;
  } catch (error) {
    console.error('Error deleting feedback:', error);
    return false;
  }
};

export const feedbackService = {
  createFeedbacks,
  getFeedbacksByTeamAndSprint,
  deleteFeedback,
};
