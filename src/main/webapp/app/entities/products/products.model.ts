import { ICategories } from 'app/entities/categories/categories.model';
import { IRestaurantDiscount } from 'app/entities/restaurant-discount/restaurant-discount.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { ICart } from 'app/entities/cart/cart.model';

export interface IProducts {
  id?: string;
  name?: string;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  estimatedPreparaingTime?: number;
  category?: ICategories | null;
  discounts?: IRestaurantDiscount[] | null;
  restaurant?: IRestaurant | null;
  cart?: ICart | null;
}

export class Products implements IProducts {
  constructor(
    public id?: string,
    public name?: string,
    public description?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public estimatedPreparaingTime?: number,
    public category?: ICategories | null,
    public discounts?: IRestaurantDiscount[] | null,
    public restaurant?: IRestaurant | null,
    public cart?: ICart | null
  ) {}
}

export function getProductsIdentifier(products: IProducts): string | undefined {
  return products.id;
}
