export interface ISimplePermission {
  id?: string;
  description?: string | null;
}

export class SimplePermission implements ISimplePermission {
  constructor(public id?: string, public description?: string | null) {}
}

export function getSimplePermissionIdentifier(simplePermission: ISimplePermission): string | undefined {
  return simplePermission.id;
}
