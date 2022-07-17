import { IPermission } from 'app/entities/permission/permission.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IRole {
  id?: string;
  description?: string | null;
  permissions?: IPermission[] | null;
  users?: IAppUser[] | null;
}

export class Role implements IRole {
  constructor(
    public id?: string,
    public description?: string | null,
    public permissions?: IPermission[] | null,
    public users?: IAppUser[] | null
  ) {}
}

export function getRoleIdentifier(role: IRole): string | undefined {
  return role.id;
}
