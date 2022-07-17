import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IPermission, getPermissionIdentifier } from '../permission.model';

export type EntityResponseType = HttpResponse<IPermission>;
export type EntityArrayResponseType = HttpResponse<IPermission[]>;

@Injectable({ providedIn: 'root' })
export class PermissionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/permissions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/permissions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(permission: IPermission): Observable<EntityResponseType> {
    return this.http.post<IPermission>(this.resourceUrl, permission, { observe: 'response' });
  }

  update(permission: IPermission): Observable<EntityResponseType> {
    return this.http.put<IPermission>(`${this.resourceUrl}/${getPermissionIdentifier(permission) as string}`, permission, {
      observe: 'response',
    });
  }

  partialUpdate(permission: IPermission): Observable<EntityResponseType> {
    return this.http.patch<IPermission>(`${this.resourceUrl}/${getPermissionIdentifier(permission) as string}`, permission, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IPermission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermission[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPermissionToCollectionIfMissing(
    permissionCollection: IPermission[],
    ...permissionsToCheck: (IPermission | null | undefined)[]
  ): IPermission[] {
    const permissions: IPermission[] = permissionsToCheck.filter(isPresent);
    if (permissions.length > 0) {
      const permissionCollectionIdentifiers = permissionCollection.map(permissionItem => getPermissionIdentifier(permissionItem)!);
      const permissionsToAdd = permissions.filter(permissionItem => {
        const permissionIdentifier = getPermissionIdentifier(permissionItem);
        if (permissionIdentifier == null || permissionCollectionIdentifiers.includes(permissionIdentifier)) {
          return false;
        }
        permissionCollectionIdentifiers.push(permissionIdentifier);
        return true;
      });
      return [...permissionsToAdd, ...permissionCollection];
    }
    return permissionCollection;
  }
}
