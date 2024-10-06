import { AxiosClient } from '@/services/index';

const saveTeamGradeFromStudent = async (
  teamNotingId: number,
  teamToNoteId: number,
  sprintId: number,
  grade: number
): Promise<boolean | null> => {
  try {
    const response = await AxiosClient.put(
      `/teamGradeFromStudent/from/${teamNotingId}/to/${teamToNoteId}/for/${sprintId}/grade/${grade}`
    );
    return response.status === 200;
  } catch (error) {
    console.error('Error creating TeamOrder:', error);
    return false;
  }
};

const getTeamOrderByTeamNotingIdAndTeamToNoteIdAndSprintId = async (
  teamNotingId: number,
  teamToNoteId: number,
  sprintId: number
) => {
  try {
    const response = await AxiosClient.get(
      `/teamGradeFromStudent/from/${teamNotingId}/to/${teamToNoteId}/for/${sprintId}`
    );
    if(!response.data){
      return {
        teamNotingId: teamNotingId,
        teamToNoteId: teamToNoteId,
        sprintId: sprintId,
        grade: null,
      };
    }
    return {
      teamNotingId: response.data.teamNotingId,
      teamToNoteId: response.data.teamToNoteId,
      sprintId: response.data.sprintId,
      grade: response.data.grade,
    };
  } catch (error) {
    console.error('Error getting TeamOrder:', error);
  }
};

export const teamGradeFromStudentService = {
  saveTeamGradeFromStudent,
  getTeamOrderByTeamNotingIdAndTeamToNoteIdAndSprintId,
};
