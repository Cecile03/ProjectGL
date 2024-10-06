import { AxiosClient } from './index';
import type { Comment } from '@/services/types';

const createComments = async (comment: Comment) => {
  try {
    const response = await AxiosClient.put('/comments', comment);
    return response.status === 200;
  } catch (error) {
    console.error('Error creating comments:', error);
    return false;
  }
};

const getCommentsByTeamAndSprint = async (
  teamId: number,
  sprintId: number
): Promise<Comment[] | null> => {
  const res = await AxiosClient.get<Comment[]>('/comments', {
    params: {
      teamId: teamId,
      sprintId: sprintId,
    },
  });
  if (!res.data.length) return null;
  return res.data;
};

const deleteComment = async (comment: Comment) => {
  try {
    const reponse = await AxiosClient.delete('/comments', {
      data: comment,
    });
    return reponse.status === 200;
  } catch (error) {
    console.error('Error deleting comment:', error);
    return false;
  }
};

export const commentService = {
  createComments,
  getCommentsByTeamAndSprint,
  deleteComment,
};
