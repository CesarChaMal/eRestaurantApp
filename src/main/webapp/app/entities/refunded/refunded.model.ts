export interface IRefunded {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class Refunded implements IRefunded {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getRefundedIdentifier(refunded: IRefunded): string | undefined {
  return refunded.id;
}
