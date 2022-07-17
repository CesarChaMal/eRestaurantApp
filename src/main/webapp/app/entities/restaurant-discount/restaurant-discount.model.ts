import { IProducts } from 'app/entities/products/products.model';

export interface IRestaurantDiscount {
  id?: string;
  code?: string;
  description?: string | null;
  percentage?: number;
  products?: IProducts | null;
}

export class RestaurantDiscount implements IRestaurantDiscount {
  constructor(
    public id?: string,
    public code?: string,
    public description?: string | null,
    public percentage?: number,
    public products?: IProducts | null
  ) {}
}

export function getRestaurantDiscountIdentifier(restaurantDiscount: IRestaurantDiscount): string | undefined {
  return restaurantDiscount.id;
}
