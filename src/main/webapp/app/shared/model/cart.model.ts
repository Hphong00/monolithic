import dayjs from 'dayjs';

export interface ICart {
  id?: string;
  userId?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<ICart> = {};
