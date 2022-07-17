import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface IRestaurantAd {
  id?: string;
  url?: string;
  description?: string | null;
  restaurant?: IRestaurant | null;
}

export class RestaurantAd implements IRestaurantAd {
  constructor(public id?: string, public url?: string, public description?: string | null, public restaurant?: IRestaurant | null) {}
}

export function getRestaurantAdIdentifier(restaurantAd: IRestaurantAd): string | undefined {
  return restaurantAd.id;
}
