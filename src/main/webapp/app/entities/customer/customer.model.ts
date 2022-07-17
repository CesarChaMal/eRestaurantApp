import { ICustomerProfile } from 'app/entities/customer-profile/customer-profile.model';
import { IOrder } from 'app/entities/order/order.model';

export interface ICustomer {
  id?: string;
  name?: string;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  email?: string | null;
  age?: number | null;
  profile?: ICustomerProfile | null;
  orders?: IOrder[] | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: string,
    public name?: string,
    public description?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public email?: string | null,
    public age?: number | null,
    public profile?: ICustomerProfile | null,
    public orders?: IOrder[] | null
  ) {}
}

export function getCustomerIdentifier(customer: ICustomer): string | undefined {
  return customer.id;
}
