export interface IOrderType {
  id?: string;
  description?: string | null;
}

export class OrderType implements IOrderType {
  constructor(public id?: string, public description?: string | null) {}
}

export function getOrderTypeIdentifier(orderType: IOrderType): string | undefined {
  return orderType.id;
}
