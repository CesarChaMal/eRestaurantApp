import { IPermission } from 'app/entities/permission/permission.model';

export interface IPermissionComposite {
  id?: string;
  description?: string | null;
  permission?: IPermission | null;
}

export class PermissionComposite implements IPermissionComposite {
  constructor(public id?: string, public description?: string | null, public permission?: IPermission | null) {}
}

export function getPermissionCompositeIdentifier(permissionComposite: IPermissionComposite): string | undefined {
  return permissionComposite.id;
}
