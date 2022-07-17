import { IUser } from 'app/entities/user/user.model';
import { IRole } from 'app/entities/role/role.model';

export interface IAppUser {
  id?: string;
  name?: string;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  email?: string | null;
  internalUser?: IUser | null;
  roles?: IRole[] | null;
}

export class AppUser implements IAppUser {
  constructor(
    public id?: string,
    public name?: string,
    public description?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public email?: string | null,
    public internalUser?: IUser | null,
    public roles?: IRole[] | null
  ) {}
}

export function getAppUserIdentifier(appUser: IAppUser): string | undefined {
  return appUser.id;
}
