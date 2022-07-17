import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IAppUser, getAppUserIdentifier } from '../app-user.model';

export type EntityResponseType = HttpResponse<IAppUser>;
export type EntityArrayResponseType = HttpResponse<IAppUser[]>;

@Injectable({ providedIn: 'root' })
export class AppUserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/app-users');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/app-users');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appUser: IAppUser): Observable<EntityResponseType> {
    return this.http.post<IAppUser>(this.resourceUrl, appUser, { observe: 'response' });
  }

  update(appUser: IAppUser): Observable<EntityResponseType> {
    return this.http.put<IAppUser>(`${this.resourceUrl}/${getAppUserIdentifier(appUser) as string}`, appUser, { observe: 'response' });
  }

  partialUpdate(appUser: IAppUser): Observable<EntityResponseType> {
    return this.http.patch<IAppUser>(`${this.resourceUrl}/${getAppUserIdentifier(appUser) as string}`, appUser, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAppUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppUser[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAppUserToCollectionIfMissing(appUserCollection: IAppUser[], ...appUsersToCheck: (IAppUser | null | undefined)[]): IAppUser[] {
    const appUsers: IAppUser[] = appUsersToCheck.filter(isPresent);
    if (appUsers.length > 0) {
      const appUserCollectionIdentifiers = appUserCollection.map(appUserItem => getAppUserIdentifier(appUserItem)!);
      const appUsersToAdd = appUsers.filter(appUserItem => {
        const appUserIdentifier = getAppUserIdentifier(appUserItem);
        if (appUserIdentifier == null || appUserCollectionIdentifiers.includes(appUserIdentifier)) {
          return false;
        }
        appUserCollectionIdentifiers.push(appUserIdentifier);
        return true;
      });
      return [...appUsersToAdd, ...appUserCollection];
    }
    return appUserCollection;
  }
}
