export interface IOrderItemProduct {
  id?: string;
  orderItemId?: string | null;
  productId?: string | null;
  quantity?: number | null;
}

export const defaultValue: Readonly<IOrderItemProduct> = {};
