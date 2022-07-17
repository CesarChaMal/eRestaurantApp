import { IRole } from 'app/entities/role/role.model';

export interface IPermission {
  id?: string;
  description?: string | null;
  roles?: IRole[] | null;
}

export class Permission implements IPermission {
  constructor(public id?: string, public description?: string | null, public roles?: IRole[] | null) {}
}

export function getPermissionIdentifier(permission: IPermission): string | undefined {
  return permission.id;
}
