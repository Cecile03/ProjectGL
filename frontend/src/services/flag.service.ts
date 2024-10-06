import { toast } from 'vue3-toastify';
import { AxiosClient } from './index';
import type { Flag, Team, User, UserFlag } from './types';;

type FlagResponse = {
    id: number;
    userId: number;
    team1Id: Team;
    team2Id: Team;
    comment: string;
    datetime: Date;
  };

  type UserFlagResponse = {
    id?: number;
    flagId: FlagResponse;
    userId: User;
    teamSwitched: boolean;
    isValidated?: boolean;
    canceledString?: string;
};


const createFlag = async (flagDTO: FlagResponse) => {
    const response = await AxiosClient.post('/flags', flagDTO);
    console.log('Flag créé avec les propriétés : ', response);
    if (!response.data) return null;

    const flagData : FlagResponse = {
      id: response.data.id,
      userId: response.data.user,
      team1Id: response.data.team1,
      team2Id: response.data.team2,
      comment: response.data.comment,
      datetime: response.data.datetime,
    };
  
    return flagData;
  };
  
  const createUserFlag = async (flag: FlagResponse, usersChanged: User[]): Promise<UserFlagResponse[]> => {
    const team1 = flag.team1Id;
    const team2 = flag.team2Id;
    const userFlags: UserFlagResponse[] = [];

    for (const team of [team1, team2]) {
      for (const user of team.users) {
        const userFlag: UserFlagResponse = {
          userId: { id: user.id }, // assuming user is an object with an id property
          flagId: { id: flag.id }, // assuming flag is an object with an id property
          teamSwitched: usersChanged ? usersChanged.some(changedUser => changedUser.id === user.id) : false,
        };
        userFlags.push(userFlag);
      }
    }
    // Post userFlags to the server
    try {
      if (userFlags.length > 0) {
        const response = await AxiosClient.post('/userFlags', userFlags);
        console.log('UserFlags créés avec les propriétés : ', response);
        //toast.success('Flag créé avec succès');
      }
      else {
        toast.success('Aucun changement à enregistrer');
        console.log("Aucun changement à enregistrer", userFlags)
      }
    }
    catch (error) {
      console.error(error);
      flagService.deleteFlagById(flag.id);
      toast.error('Erreur lors de la création du flag');
      console.log('Flag supprimé', flag);
      throw error;
    }
    return userFlags;
  };

  const getFlagById = async (id: number): Promise<FlagResponse | null> => {
    try {
      const response = await AxiosClient.get<FlagResponse>(`/flags/${id}`);
      
      return response.data;
    } catch (error) {
      console.error(error);
      throw error;
    }
  };
  
  
  const getFlags = async (): Promise<Flag[] | null> => {
    try {
      const response = await AxiosClient.get<Flag[]>('/flags');
      return response.data;
    } catch (error) {
      console.error(error);
      toast.error("Erreur lors de la récupération des flags");
      throw error;
    }
  };

  const getUserFlagByFlagId = async (flagId: number): Promise<UserFlag[] | null> => {
    if (flagId === 0) return null;
    try {
      const response = await AxiosClient.get<UserFlag[]>(`/userFlags/flag/${flagId}`);
      return response.data;
    } catch (error) {
      console.error(error);
      toast.error("Erreur lors de la récupération des flags des utilisateurs");
      throw error;
    }
  };


  /**
   * Cette méthode Delete un Flag ainsi que les UserFlag associés
   * @param id : id du flag
   */
  const deleteFlagById = async (id: number): Promise<void> => {
    try {
      console.log("id du flag : ", 8);
      await AxiosClient.delete(`/userFlags/flag/${id}`);
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la suppression du UserFlag');
      throw error;
    } finally {
      try {
        await AxiosClient.delete(`/flags/${id}`);
      } catch (error2) {
        console.error(error2);
        toast.error('Erreur lors de la suppression du flag');
        // eslint-disable-next-line no-unsafe-finally
        throw error2;
      }
    }
};

  const setUserFlagValidated = async (id: number, validated: boolean): Promise<UserFlag | null> => {
    if (id === 0) return null;
    try {
      const response = await AxiosClient.put(`/userFlags/${id}/validated`, validated, 
      {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      console.log('UserFlag validé avec les propriétés : ', response);
      return response.data;
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de la validation du UserFlag');
      throw error;
    }
  };

  const areAllUserFlagsValidated = async (flagId: number): Promise<boolean> => {
    try {
      const response = await AxiosClient.get<boolean>(`/userFlags/validated/${flagId}`);
      return response.data;
    } catch (error) {
      console.error(error);
      toast.error("Erreur lors de la vérification de la validation des UserFlags");
      throw error;
    }
  };
  
  export const flagService = {
    createFlag,
    getFlags,
    createUserFlag,
    getFlagById,
    deleteFlagById,
    getUserFlagByFlagId,
    setUserFlagValidated,
    areAllUserFlagsValidated
  };

  export type { FlagResponse, UserFlagResponse };