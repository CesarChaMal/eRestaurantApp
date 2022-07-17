import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IAdmin, getAdminIdentifier } from '../admin.model';

export type EntityResponseType = HttpResponse<IAdmin>;
export type EntityArrayResponseType = HttpResponse<IAdmin[]>;

@Injectable({ providedIn: 'root' })
export class AdminService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/admins');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/admins');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(admin: IAdmin): Observable<EntityResponseType> {
    return this.http.post<IAdmin>(this.resourceUrl, admin, { observe: 'response' });
  }

  update(admin: IAdmin): Observable<EntityResponseType> {
    return this.http.put<IAdmin>(`${this.resourceUrl}/${getAdminIdentifier(admin) as string}`, admin, { observe: 'response' });
  }

  partialUpdate(admin: IAdmin): Observable<EntityResponseType> {
    return this.http.patch<IAdmin>(`${this.resourceUrl}/${getAdminIdentifier(admin) as string}`, admin, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAdmin>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAdmin[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAdmin[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAdminToCollectionIfMissing(adminCollection: IAdmin[], ...adminsToCheck: (IAdmin | null | undefined)[]): IAdmin[] {
    const admins: IAdmin[] = adminsToCheck.filter(isPresent);
    if (admins.length > 0) {
      const adminCollectionIdentifiers = adminCollection.map(adminItem => getAdminIdentifier(adminItem)!);
      const adminsToAdd = admins.filter(adminItem => {
        const adminIdentifier = getAdminIdentifier(adminItem);
        if (adminIdentifier == null || adminCollectionIdentifiers.includes(adminIdentifier)) {
          return false;
        }
        adminCollectionIdentifiers.push(adminIdentifier);
        return true;
      });
      return [...adminsToAdd, ...adminCollection];
    }
    return adminCollection;
  }
}
