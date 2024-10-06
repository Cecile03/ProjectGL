import type { promises } from 'dns';
import { AxiosClient } from '.';
import { formatUserResponse, type UserResponse } from './auth.service';
import type { Notification } from './types';

type NotificationResponse = {
  id: number;
  type: string;
  status: 'READ' | 'UNREAD';
  description: string;
  date: Date;
  emitter: UserResponse;
  receiver: UserResponse;
  groupeId: number;
};

type NotificationSend = {
  id?: number;
  type: string;
  status: 'READ' | 'UNREAD';
  description: string;
  date: Date;
  emitterId: number;
  receiverId: number;
  groupeId?: number;
};

const formatNotifResponse = (data: NotificationResponse): Notification => {
  return {
    id: data.id,
    date: data.date,
    type: data.type,
    status: data.status,
    description: data.description,
    emitter: formatUserResponse(data.emitter),
    receiver: formatUserResponse(data.receiver),
    groupeId: data.groupeId,
  };
};

const getAllByUserId = async (
  userId: number
): Promise<Notification[] | null> => {
  const res = await AxiosClient.get<NotificationResponse[]>(
    `/notifications/${userId}`
  );
  if (!res.data.length) return null;
  return res.data.map((notif) => formatNotifResponse(notif));
};

const deleteById = (notifId: number) => {
  return AxiosClient.delete(`/notifications/${notifId}`);
};

const updateNotification = (notif: any) => {
  const row = notif;
  return AxiosClient.put(`/notifications`, row);
};

const toggleStatus = async (id: number): Promise<Notification | null> => {
  try {
    const res = await AxiosClient.put<NotificationResponse>(
      `/notifications/${id}`
    );
    return formatNotifResponse(res.data);
  } catch (error) {
    return null;
  }
};

const createNotification = async (notif: NotificationSend) => {
  try {
    const response = await AxiosClient.post('/notifications', notif);
    return response.data;
  } catch (error) {
    console.error('Failed to create notification:', error);
    throw error;
  }
};

export const notificationService = {
  getAllByUserId,
  deleteById,
  toggleStatus,
  updateNotification,
  createNotification
};
