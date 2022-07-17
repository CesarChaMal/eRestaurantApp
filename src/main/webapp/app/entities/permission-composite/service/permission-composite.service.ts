import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IPermissionComposite, getPermissionCompositeIdentifier } from '../permission-composite.model';

export type EntityResponseType = HttpResponse<IPermissionComposite>;
export type EntityArrayResponseType = HttpResponse<IPermissionComposite[]>;

@Injectable({ providedIn: 'root' })
export class PermissionCompositeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/permission-composites');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/permission-composites');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(permissionComposite: IPermissionComposite): Observable<EntityResponseType> {
    return this.http.post<IPermissionComposite>(this.resourceUrl, permissionComposite, { observe: 'response' });
  }

  update(permissionComposite: IPermissionComposite): Observable<EntityResponseType> {
    return this.http.put<IPermissionComposite>(
      `${this.resourceUrl}/${getPermissionCompositeIdentifier(permissionComposite) as string}`,
      permissionComposite,
      { observe: 'response' }
    );
  }

  partialUpdate(permissionComposite: IPermissionComposite): Observable<EntityResponseType> {
    return this.http.patch<IPermissionComposite>(
      `${this.resourceUrl}/${getPermissionCompositeIdentifier(permissionComposite) as string}`,
      permissionComposite,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IPermissionComposite>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermissionComposite[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermissionComposite[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPermissionCompositeToCollectionIfMissing(
    permissionCompositeCollection: IPermissionComposite[],
    ...permissionCompositesToCheck: (IPermissionComposite | null | undefined)[]
  ): IPermissionComposite[] {
    const permissionComposites: IPermissionComposite[] = permissionCompositesToCheck.filter(isPresent);
    if (permissionComposites.length > 0) {
      const permissionCompositeCollectionIdentifiers = permissionCompositeCollection.map(
        permissionCompositeItem => getPermissionCompositeIdentifier(permissionCompositeItem)!
      );
      const permissionCompositesToAdd = permissionComposites.filter(permissionCompositeItem => {
        const permissionCompositeIdentifier = getPermissionCompositeIdentifier(permissionCompositeItem);
        if (permissionCompositeIdentifier == null || permissionCompositeCollectionIdentifiers.includes(permissionCompositeIdentifier)) {
          return false;
        }
        permissionCompositeCollectionIdentifiers.push(permissionCompositeIdentifier);
        return true;
      });
      return [...permissionCompositesToAdd, ...permissionCompositeCollection];
    }
    return permissionCompositeCollection;
  }
}
