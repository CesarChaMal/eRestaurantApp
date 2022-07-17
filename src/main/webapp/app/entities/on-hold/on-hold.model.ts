export interface IOnHold {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class OnHold implements IOnHold {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getOnHoldIdentifier(onHold: IOnHold): string | undefined {
  return onHold.id;
}
