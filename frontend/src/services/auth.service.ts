import { AxiosClient } from './index';
import type { User } from './types';

interface LoginResponse {
  token: string;
}

export type UserResponse = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  gender: string;
  option: string;
  bachelor: boolean;
  authorities: { authority: string }[];
  gradePast: number;
  // team: Object;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  enabled: boolean;
};

export const formatUserResponse = (userResponse: UserResponse): User => {
  return {
    id: userResponse.id,
    firstName: userResponse.firstName,
    lastName: userResponse.lastName,
    email: userResponse.email,
    roles: userResponse.authorities ? userResponse.authorities.map((authority) => {
      // mapping userResponse to User type in whole frontend
      switch (authority.authority) {
        case 'SS':
          return 'SS';
        case 'PL':
          return 'PL';
        case 'OL':
          return 'OL';
        case 'OS':
          return 'OS';
        case 'TC':
          return 'TC';
        default:
          return 'OS';
      }
    }) : [],
    gender: userResponse.gender === 'male' ? 'male' : 'female',
    option: userResponse.option,
    isBachelor: userResponse.bachelor,
    gradePast: userResponse.gradePast,
  };
};

const login = async (
  email: string,
  password: string
): Promise<string | null> => {
  const { data } = await AxiosClient.post<LoginResponse>('/auth/signin', {
    email,
    password,
  });

  const token = data.token;
  if (!token) {
    return null;
  } else {
    localStorage.setItem('token', token);
    return token;
  }
};

const getMe = async (): Promise<User | null> => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }

    const response = await AxiosClient.get<UserResponse>('/auth/getme');

    return formatUserResponse(response.data);
  } catch (error) {
    console.error(
      "Erreur lors de la récupération des détails de l'utilisateur:",
      error
    );
    return null;
  }
};

export const authService = { login, getMe };
