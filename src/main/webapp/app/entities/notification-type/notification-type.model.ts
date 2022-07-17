export interface INotificationType {
  id?: string;
  description?: string | null;
}

export class NotificationType implements INotificationType {
  constructor(public id?: string, public description?: string | null) {}
}

export function getNotificationTypeIdentifier(notificationType: INotificationType): string | undefined {
  return notificationType.id;
}
