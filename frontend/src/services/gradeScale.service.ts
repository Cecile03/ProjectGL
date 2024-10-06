import { AxiosClient } from '@/services/index';

interface Detail {
  name: string;
  mark: number;
}

interface Category {
  name: string;
  details: Detail[];
}

interface GradeScale {
  name: string;
  categories: Category[];
}

const createGradeScale = async (
  gradeScaleData: GradeScale[]
): Promise<boolean> => {
  try {
    const response = await AxiosClient.post('/gradeScale', gradeScaleData);

    return response.status === 200;

  } catch (error) {
    console.error('Error creating grade scale:', error);
    return false;
  }
};

const getGradeScaleMarks = async () => {
  const response = await AxiosClient.get(`/gradeScale/1`);
  if(response.data == null){
    return null;
  }
  return {
    id: response.data.id,
    name: response.data.name,
    description: response.data.description,
  };
};

const getGradeScaleProject = async () => {
  const response = await AxiosClient.get(`/gradeScale/2`);
  if(response.data == null){
    return null;
  }
  return {
    id: response.data.id,
    name: response.data.name,
    description: response.data.description,
  };
};

const getGradeScalePresentation = async () => {
  const response = await AxiosClient.get(`/gradeScale/3`);
  if(response.data == null){
    return null;
  }
  return {
    id: response.data.id,
    name: response.data.name,
    description: response.data.description,
  };
};

const getAllGradeScales = async (): Promise<Array<any> | null> => {
  try {
    const response = await AxiosClient.get('/gradeScale');
    if (response.data) {
      if (Array.isArray(response.data)) {
        return response.data;
      }
      if (typeof response.data === 'object') {
        return [response.data];
      }
    }
    return null;
  } catch (error) {
    console.error('Error getting grade scales:', error);
    return null;
  }
};

const deleteAllGradeScales = async () => {
  try {
    const response = await AxiosClient.delete('/gradeScale');
    return response.status === 200;
  } catch (error) {
    console.error('Error deleting all grade scales:', error);
    return false;
  }
};

export const gradeScaleService = {
  getGradeScaleProject,
  createGradeScale,
  getGradeScaleMarks,
  getGradeScalePresentation,
  getAllGradeScales,
  deleteAllGradeScales,
};
