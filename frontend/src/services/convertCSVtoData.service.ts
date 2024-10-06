import { AxiosClient } from './index';

export const fetchData = async (file: File) => {
  try {
    const formData = new FormData();
    formData.append('file', file); // The file to send

    const response = await AxiosClient.post(`/data/csv`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.data;
  } catch (error) {
    console.error('Error fetching data:', error);
    throw error;
  }
};
