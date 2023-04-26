export interface ICartItem {
  id?: string;
  cartId?: string | null;
  productId?: string | null;
  quantity?: number | null;
}

export const defaultValue: Readonly<ICartItem> = {};
