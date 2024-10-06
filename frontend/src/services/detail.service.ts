import { AxiosClient } from '@/services/index';

const createDetail = async (detailData: any): Promise<Array<any> | null> => {
  try {
    const response = await AxiosClient.post('/details', detailData);
    // Check if response.data is defined
    if (response.data) {
      // Check if response.data is an array
      if (Array.isArray(response.data)) {
        return response.data;
      }

      // If response.data is an object, convert it to an array
      if (typeof response.data === 'object') {
        return [response.data];
      }
    }
    // If response.data is undefined or neither an array nor an object, return null
    return null;
  } catch (error) {
    console.error('Error creating detail:', error);
    return null;
  }
};

const updateDetail = async (
  id: number,
  detailData: any
): Promise<Array<any> | null> => {
  try {
    const response = await AxiosClient.put(`/details/${id}`, detailData);
    console.log('API response update : ', response);
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
    console.error('Error updating detail:', error);
    return null;
  }
};

const getAllDetails = async (): Promise<Array<any> | null> => {
  try {
    const response = await AxiosClient.get('/details');
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
    console.error('Error getting details:', error);
    return null;
  }
};

const deleteDetail = async (id: number): Promise<Array<any> | null> => {
  try {
    const response = await AxiosClient.delete(`/details/${id}`);
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
    console.error('Error deleting detail:', error);
    return null;
  }
};

const getDetails = async () => {
  const response = await AxiosClient.get(`/details`);
  const detailsData = response.data.map((detail: any) => ({
    id: detail.id,
    name: detail.name,
    description: detail.description,
  }));
  return detailsData;
};

const getDetailsByCategoryId = async (categoryId: number) => {
  const response = await AxiosClient.get(`/details/${categoryId}/category`);
  const detailsData = response.data.map((detail: any) => ({
    id: detail.id,
    name: detail.name,
    description: detail.description,
    mark: detail.mark,
  }));
  return detailsData;
};

const deleteAllDetails = async () => {
  try {
    const response = await AxiosClient.delete(`/details`);
    return response.status === 200;
  } catch (error) {
    console.error('Error deleting all details:', error);
    return false;
  }
};
export const detailService = {
  getDetails,
  getDetailsByCategoryId,
  deleteDetail,
  getAllDetails,
  createDetail,
  updateDetail,
  deleteAllDetails,
};
