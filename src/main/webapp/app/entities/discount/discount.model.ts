export interface IDiscount {
  id?: string;
  code?: string;
  description?: string | null;
  percentage?: number;
}

export class Discount implements IDiscount {
  constructor(public id?: string, public code?: string, public description?: string | null, public percentage?: number) {}
}

export function getDiscountIdentifier(discount: IDiscount): string | undefined {
  return discount.id;
}
