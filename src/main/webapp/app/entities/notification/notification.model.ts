import { INotificationType } from 'app/entities/notification-type/notification-type.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface INotification {
  id?: string;
  description?: string | null;
  type?: INotificationType | null;
  restaurant?: IRestaurant | null;
}

export class Notification implements INotification {
  constructor(
    public id?: string,
    public description?: string | null,
    public type?: INotificationType | null,
    public restaurant?: IRestaurant | null
  ) {}
}

export function getNotificationIdentifier(notification: INotification): string | undefined {
  return notification.id;
}
