import dayjs from 'dayjs';

export interface INotification {
  id?: string;
  userId?: string | null;
  orderId?: string | null;
  message?: string | null;
  type?: string | null;
  isRead?: boolean | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<INotification> = {
  isRead: false,
};
