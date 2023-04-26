import dayjs from 'dayjs';

export interface IOrder {
  id?: string;
  userId?: string | null;
  shippingId?: string | null;
  paymentId?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<IOrder> = {};
