import { IOrder } from 'app/entities/order/order.model';
import { IProducts } from 'app/entities/products/products.model';
import { IAppDiscount } from 'app/entities/app-discount/app-discount.model';
import { IPayment } from 'app/entities/payment/payment.model';

export interface ICart {
  id?: string;
  description?: string | null;
  order?: IOrder | null;
  products?: IProducts[] | null;
  discounts?: IAppDiscount[] | null;
  payments?: IPayment[] | null;
}

export class Cart implements ICart {
  constructor(
    public id?: string,
    public description?: string | null,
    public order?: IOrder | null,
    public products?: IProducts[] | null,
    public discounts?: IAppDiscount[] | null,
    public payments?: IPayment[] | null
  ) {}
}

export function getCartIdentifier(cart: ICart): string | undefined {
  return cart.id;
}
