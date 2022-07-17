export interface INewOrder {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class NewOrder implements INewOrder {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getNewOrderIdentifier(newOrder: INewOrder): string | undefined {
  return newOrder.id;
}
