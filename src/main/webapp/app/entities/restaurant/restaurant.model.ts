import { IProducts } from 'app/entities/products/products.model';
import { IRestaurantAd } from 'app/entities/restaurant-ad/restaurant-ad.model';
import { IEmployee } from 'app/entities/employee/employee.model';
import { INotification } from 'app/entities/notification/notification.model';

export interface IRestaurant {
  id?: string;
  name?: string;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  email?: string | null;
  rating?: number;
  products?: IProducts[] | null;
  ads?: IRestaurantAd[] | null;
  employees?: IEmployee[] | null;
  notifications?: INotification[] | null;
}

export class Restaurant implements IRestaurant {
  constructor(
    public id?: string,
    public name?: string,
    public description?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public email?: string | null,
    public rating?: number,
    public products?: IProducts[] | null,
    public ads?: IRestaurantAd[] | null,
    public employees?: IEmployee[] | null,
    public notifications?: INotification[] | null
  ) {}
}

export function getRestaurantIdentifier(restaurant: IRestaurant): string | undefined {
  return restaurant.id;
}
