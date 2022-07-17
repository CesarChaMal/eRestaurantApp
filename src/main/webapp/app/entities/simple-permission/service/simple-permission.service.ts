import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISimplePermission, getSimplePermissionIdentifier } from '../simple-permission.model';

export type EntityResponseType = HttpResponse<ISimplePermission>;
export type EntityArrayResponseType = HttpResponse<ISimplePermission[]>;

@Injectable({ providedIn: 'root' })
export class SimplePermissionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/simple-permissions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/simple-permissions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(simplePermission: ISimplePermission): Observable<EntityResponseType> {
    return this.http.post<ISimplePermission>(this.resourceUrl, simplePermission, { observe: 'response' });
  }

  update(simplePermission: ISimplePermission): Observable<EntityResponseType> {
    return this.http.put<ISimplePermission>(
      `${this.resourceUrl}/${getSimplePermissionIdentifier(simplePermission) as string}`,
      simplePermission,
      { observe: 'response' }
    );
  }

  partialUpdate(simplePermission: ISimplePermission): Observable<EntityResponseType> {
    return this.http.patch<ISimplePermission>(
      `${this.resourceUrl}/${getSimplePermissionIdentifier(simplePermission) as string}`,
      simplePermission,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ISimplePermission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISimplePermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISimplePermission[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSimplePermissionToCollectionIfMissing(
    simplePermissionCollection: ISimplePermission[],
    ...simplePermissionsToCheck: (ISimplePermission | null | undefined)[]
  ): ISimplePermission[] {
    const simplePermissions: ISimplePermission[] = simplePermissionsToCheck.filter(isPresent);
    if (simplePermissions.length > 0) {
      const simplePermissionCollectionIdentifiers = simplePermissionCollection.map(
        simplePermissionItem => getSimplePermissionIdentifier(simplePermissionItem)!
      );
      const simplePermissionsToAdd = simplePermissions.filter(simplePermissionItem => {
        const simplePermissionIdentifier = getSimplePermissionIdentifier(simplePermissionItem);
        if (simplePermissionIdentifier == null || simplePermissionCollectionIdentifiers.includes(simplePermissionIdentifier)) {
          return false;
        }
        simplePermissionCollectionIdentifiers.push(simplePermissionIdentifier);
        return true;
      });
      return [...simplePermissionsToAdd, ...simplePermissionCollection];
    }
    return simplePermissionCollection;
  }
}
