export interface ICancel {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class Cancel implements ICancel {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getCancelIdentifier(cancel: ICancel): string | undefined {
  return cancel.id;
}
