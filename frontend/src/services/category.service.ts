import { AxiosClient } from '@/services/index';

const getCategories = async () => {
  const response = await AxiosClient.get('/categories');
  return response.data.map((category: any) => ({
    id: category.id,
    name: category.name,
    description: category.description,
  }));
};

const getCategoriesByGradeScaleId = async (gradeScaleId: number) => {
  const response = await AxiosClient.get(
    `/categories/${gradeScaleId}/gradeScale`
  );
  return response.data.map((category: any) => ({
    id: category.id,
    name: category.name,
    description: category.description,
  }));
};

const deleteAllCategories = async () => {
  try {
    const response = await AxiosClient.delete('/categories');
    return response.status === 200;
  } catch (error) {
    console.error('Error deleting all categories:', error);
    return false;
  }
};

export const categoryService = {
  getCategories,
  getCategoriesByGradeScaleId,
  deleteAllCategories,
};
