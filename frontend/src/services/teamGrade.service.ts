import { AxiosClient } from '@/services/index';

const saveTeamGrade = async (
  teamId: number,
  sprintId: number,
  detailId: number,
  evaluatorId: number,
  grade: number
): Promise<boolean | null> => {
  try {
    const response = await AxiosClient.put(
      `/teamGrade`,
      {},
      {
        params: {
          teamId: teamId,
          sprintId: sprintId,
          detailId: detailId,
          evaluatorId: evaluatorId,
          grade: grade,
        },
      }
    );
    return response.status === 200;
  } catch (error) {
    console.error('Error saving TeamGrade:', error);
    return false;
  }
};

const getTeamGrade = async (
  teamId: number,
  sprintId: number,
  detailId: number,
  evaluatorId: number
) => {
  try {
    const response = await AxiosClient.get(`/teamGrade`, {
      params: {
        teamId: teamId,
        sprintId: sprintId,
        detailId: detailId,
        evaluatorId: evaluatorId,
      },
    });
    if (response.data) {
      return {
        teamId: response.data.teamId,
        sprintId: response.data.sprintId,
        detailId: response.data.detailId,
        evaluatorId: response.data.evaluatorId,
        grade: response.data.grade,
      };
    } else {
      return {
        teamId: teamId,
        sprintId: sprintId,
        detailId: detailId,
        evaluatorId: evaluatorId,
        grade: 0,
      };
    }
  } catch (error) {
    console.error('Error getting TeamGrade:', error);
    return null;
  }
};

export const teamGradeService = {
  saveTeamGrade,
  getTeamGrade,
};
