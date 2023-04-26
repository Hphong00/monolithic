import dayjs from 'dayjs';

export interface IOrderItem {
  id?: string;
  orderId?: string | null;
  productId?: string | null;
  quantity?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<IOrderItem> = {};
