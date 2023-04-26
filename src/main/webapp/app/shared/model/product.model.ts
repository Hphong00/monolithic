import dayjs from 'dayjs';

export interface IProduct {
  id?: string;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<IProduct> = {};
