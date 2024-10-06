import type {
  BonusMalus,
  BonusMalusStatus,
} from '@/views/Notation/BonusMalus.vue';
import { AxiosClient } from '.';

type BonusMalusResponse = {
  id: number;
  value: number;
  comment: string;
  status: BonusMalusStatus;
  attributedTo: number;
  attributedBy: number;
  teamId: number;
  sprintId: number;
  unlimited: boolean;
};

const formatBonusMalusResponse = (req: BonusMalusResponse[]): BonusMalus[] => {
  const bmList: BonusMalus[] = req.map((bm) => ({
    id: bm.id,
    value: bm.value,
    comment: bm.comment,
    attributedTo: bm.attributedTo,
    attributedBy: bm.attributedBy,
    status: bm.status,
    teamId: bm.teamId,
    sprintId: bm.sprintId,
    isUnlimited: bm.unlimited,
  }));

  return bmList;
};

const getTeamBM = async (
  unlimited: boolean,
  teamId: number,
  sprintId: number
): Promise<BonusMalus[] | null> => {
  const path = `/bonusMalus/${teamId}/${unlimited ? 'unlimited' : 'limited'}?sprintId=${sprintId}`;
  const res = await AxiosClient.get<BonusMalusResponse[]>(path);

  if (!res.data.length) return null;
  else return formatBonusMalusResponse(res.data);
};

const addBonusMalus = (
  teamId: number,
  bonusMalusList: any,
  sprintId: number
) => {
  return AxiosClient.post(
    `/bonusMalus/${teamId}?sprintId=${sprintId}`,
    bonusMalusList
  );
};

const validateBonusLMalus = (teamId: number, sprintId: number) => {
  return AxiosClient.post(
    `/bonusMalus/${teamId}/validate?sprintId=${sprintId}`
  );
};

const getMembersWhoValidateBM = async (
  teamId: number,
  sprintId: number
): Promise<number[] | null> => {
  const res = await AxiosClient.get<number[]>(
    `/bonusMalus/${teamId}/validate?sprintId=${sprintId}`
  );
  if (!res.data.length) return null;
  return res.data;
};

const getUserBMForSprintByStudent = async (
  userId: number,
  sprintId: number
): Promise<BonusMalus | null> => {
  try {
    const res = await AxiosClient.get<BonusMalusResponse>(
      `/bonusMalus/student?userId=${userId}&sprintId=${sprintId}`
    );
    if (!res.data) return null;
    else return formatBonusMalusResponse([res.data])[0]; // Wrap the response data in an array and take the first element
  } catch (error) {
    console.error(error);
    return null;
  }
};

const getUserBMForSprintBySS = async (
  userId: number,
  sprintId: number
): Promise<BonusMalus | null> => {
  try {
    const res = await AxiosClient.get<BonusMalusResponse>(
      `/bonusMalus/teacher?userId=${userId}&sprintId=${sprintId}`
    );
    if (!res.data) return null;
    else return formatBonusMalusResponse([res.data])[0]; // Wrap the response data in an array and take the first element
  } catch (error) {
    console.error(error);
    return null;
  }
};

export const bonusMalusService = {
  getTeamBM,
  addBonusMalus,
  validateBonusLMalus,
  getMembersWhoValidateBM,
  getUserBMForSprintByStudent,
  getUserBMForSprintBySS,
};
