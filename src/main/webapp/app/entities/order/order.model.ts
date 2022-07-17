import { IOrderType } from 'app/entities/order-type/order-type.model';
import { IState } from 'app/entities/state/state.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface IOrder {
  id?: string;
  description?: string | null;
  rating?: number;
  type?: IOrderType | null;
  state?: IState | null;
  customer?: ICustomer | null;
}

export class Order implements IOrder {
  constructor(
    public id?: string,
    public description?: string | null,
    public rating?: number,
    public type?: IOrderType | null,
    public state?: IState | null,
    public customer?: ICustomer | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): string | undefined {
  return order.id;
}
