export interface IClose {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class Close implements IClose {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getCloseIdentifier(close: IClose): string | undefined {
  return close.id;
}
