import { AxiosClient } from '@/services/index';

const saveTeamOrder = async (
  teamId: number,
  sprintId: number,
  userIds: number[]
): Promise<boolean | null> => {
  try {
    const response = await AxiosClient.put(
      `/teamOrder/for/${teamId}/during/${sprintId}`,
      userIds
    );
    return response.status === 200;
  } catch (error) {
    console.error('Error creating TeamOrder:', error);
    return false;
  }
};

const getTeamOrderByTeamIdAndSprintId = async (
  teamId: number,
  sprintId: number
) => {
  try {
    const response = await AxiosClient.get(
      `/teamOrder/for/${teamId}/during/${sprintId}`
    );
    if(!response.data){
      return {
        teamId: teamId,
        sprintId: sprintId,
        order: [],
      };
    }
    return {
      teamId: response.data.teamId,
      sprintId: response.data.sprintId,
      order: response.data.order,
    };
  } catch (error) {
    console.error('Error getting TeamOrder:', error);
    return null;
  }
};

export const teamOrderService = {
  saveTeamOrder,
  getTeamOrderByTeamIdAndSprintId,
};
