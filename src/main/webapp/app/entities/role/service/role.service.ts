import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRole, getRoleIdentifier } from '../role.model';

export type EntityResponseType = HttpResponse<IRole>;
export type EntityArrayResponseType = HttpResponse<IRole[]>;

@Injectable({ providedIn: 'root' })
export class RoleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/roles');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/roles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(role: IRole): Observable<EntityResponseType> {
    return this.http.post<IRole>(this.resourceUrl, role, { observe: 'response' });
  }

  update(role: IRole): Observable<EntityResponseType> {
    return this.http.put<IRole>(`${this.resourceUrl}/${getRoleIdentifier(role) as string}`, role, { observe: 'response' });
  }

  partialUpdate(role: IRole): Observable<EntityResponseType> {
    return this.http.patch<IRole>(`${this.resourceUrl}/${getRoleIdentifier(role) as string}`, role, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRole>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRole[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRole[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addRoleToCollectionIfMissing(roleCollection: IRole[], ...rolesToCheck: (IRole | null | undefined)[]): IRole[] {
    const roles: IRole[] = rolesToCheck.filter(isPresent);
    if (roles.length > 0) {
      const roleCollectionIdentifiers = roleCollection.map(roleItem => getRoleIdentifier(roleItem)!);
      const rolesToAdd = roles.filter(roleItem => {
        const roleIdentifier = getRoleIdentifier(roleItem);
        if (roleIdentifier == null || roleCollectionIdentifiers.includes(roleIdentifier)) {
          return false;
        }
        roleCollectionIdentifiers.push(roleIdentifier);
        return true;
      });
      return [...rolesToAdd, ...roleCollection];
    }
    return roleCollection;
  }
}
