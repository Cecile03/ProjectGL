import { AxiosClient } from '.';
import { formatUserResponse, type UserResponse } from './auth.service';
import type { User } from './types';

const getUsers = async () => {
  const res = await AxiosClient.get<UserResponse[]>('/users');
  console.log('getUsers : ', res.data);
  const users: User[] = [];
  for (let i = 0; i < res.data.length; i++) {
    const user: User = formatUserResponse(res.data[i]);
    users.push(user);
  }
  return users;
};

const getStudents = async (): Promise<User[] | null> => {
  const res = await AxiosClient.get<UserResponse[]>('/users/students');

  if (!res.data.length) return null;

  const students: User[] = [];
  for (let i = 0; i < res.data.length; i++) {
    const user: User = formatUserResponse(res.data[i]);
    students.push(user);
  }
  return students;
};

const deleteStudents = () => {
  return AxiosClient.delete('/users/students');
};

const createStudentList = (students: User[]) => {
  return AxiosClient.post('/users/students', students);
};

const getTeachers = async (): Promise<User[]> => {
  const res = await AxiosClient.get<UserResponse[]>('/users/teachers');
  console.log('getTeachers: ', res.data);
  const users: User[] = [];
  for (let i = 0; i < res.data.length; i++) {
    const user: User = formatUserResponse(res.data[i]);
    users.push(user);
  }
  return users;
};

const getStaff = async (): Promise<User[]> => {
  const res = await AxiosClient.get<UserResponse[]>('/users/staff');
  console.log('getStaff: ', res.data);
  const users: User[] = [];
  for (let i = 0; i < res.data.length; i++) {
    const user: User = formatUserResponse(res.data[i]);
    users.push(user);
  }
  return users;
}

const getUserById = async (id: number): Promise<User> => {
  const res = await AxiosClient.get<User>(`/users/${id}`);
  return res.data;
};

export const userService = {
  getUsers,
  getTeachers,
  getStudents,
  deleteStudents,
  createStudentList,
  getUserById,
  getStaff
};
