import { IAdmin } from 'app/entities/admin/admin.model';

export interface IAppAd {
  id?: string;
  url?: string;
  description?: string | null;
  admin?: IAdmin | null;
}

export class AppAd implements IAppAd {
  constructor(public id?: string, public url?: string, public description?: string | null, public admin?: IAdmin | null) {}
}

export function getAppAdIdentifier(appAd: IAppAd): string | undefined {
  return appAd.id;
}
