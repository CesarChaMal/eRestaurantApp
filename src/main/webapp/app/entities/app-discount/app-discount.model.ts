import { ICart } from 'app/entities/cart/cart.model';

export interface IAppDiscount {
  id?: string;
  code?: string;
  description?: string | null;
  percentage?: number;
  cart?: ICart | null;
}

export class AppDiscount implements IAppDiscount {
  constructor(
    public id?: string,
    public code?: string,
    public description?: string | null,
    public percentage?: number,
    public cart?: ICart | null
  ) {}
}

export function getAppDiscountIdentifier(appDiscount: IAppDiscount): string | undefined {
  return appDiscount.id;
}
