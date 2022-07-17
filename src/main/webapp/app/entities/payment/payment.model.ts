import { ICart } from 'app/entities/cart/cart.model';

export interface IPayment {
  id?: string;
  description?: string | null;
  cart?: ICart | null;
}

export class Payment implements IPayment {
  constructor(public id?: string, public description?: string | null, public cart?: ICart | null) {}
}

export function getPaymentIdentifier(payment: IPayment): string | undefined {
  return payment.id;
}
