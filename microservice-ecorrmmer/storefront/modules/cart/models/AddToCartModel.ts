export type AddToCartModel = {
  productId: string;
  quantity: number;
  parentProductId?: string | null;
};
