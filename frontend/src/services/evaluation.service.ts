// EvaluationService.ts
import { AxiosClient } from '@/services/index';
import type { Evaluation } from '@/services/types';

const API_URL = '/evaluation';

const updateEvaluation = async (evaluationId: number, value: number) : Promise<Evaluation> => {
  try {
    const response = await AxiosClient.put(`${API_URL}/update/${evaluationId}/${value}`);
    if(response.status !== 200) throw new Error('Failed to update evaluation')
    return response.data;
  } catch (error) {
    console.error('Failed to update evaluation:', error);
    throw error;
  }
}

export const evaluationService = { updateEvaluation };
