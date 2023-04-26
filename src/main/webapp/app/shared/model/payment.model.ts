import dayjs from 'dayjs';

export interface IPayment {
  id?: string;
  userId?: string | null;
  cardNumber?: string | null;
  cardHolderName?: string | null;
  cardExpirationMonth?: number | null;
  cardExpirationYear?: number | null;
  cardCVV?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<IPayment> = {};
