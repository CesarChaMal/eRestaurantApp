export interface IComplete {
  id?: string;
  description?: string | null;
  enabled?: boolean | null;
}

export class Complete implements IComplete {
  constructor(public id?: string, public description?: string | null, public enabled?: boolean | null) {
    this.enabled = this.enabled ?? false;
  }
}

export function getCompleteIdentifier(complete: IComplete): string | undefined {
  return complete.id;
}
