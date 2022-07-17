import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOnHold, getOnHoldIdentifier } from '../on-hold.model';

export type EntityResponseType = HttpResponse<IOnHold>;
export type EntityArrayResponseType = HttpResponse<IOnHold[]>;

@Injectable({ providedIn: 'root' })
export class OnHoldService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/on-holds');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/on-holds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(onHold: IOnHold): Observable<EntityResponseType> {
    return this.http.post<IOnHold>(this.resourceUrl, onHold, { observe: 'response' });
  }

  update(onHold: IOnHold): Observable<EntityResponseType> {
    return this.http.put<IOnHold>(`${this.resourceUrl}/${getOnHoldIdentifier(onHold) as string}`, onHold, { observe: 'response' });
  }

  partialUpdate(onHold: IOnHold): Observable<EntityResponseType> {
    return this.http.patch<IOnHold>(`${this.resourceUrl}/${getOnHoldIdentifier(onHold) as string}`, onHold, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IOnHold>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOnHold[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOnHold[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOnHoldToCollectionIfMissing(onHoldCollection: IOnHold[], ...onHoldsToCheck: (IOnHold | null | undefined)[]): IOnHold[] {
    const onHolds: IOnHold[] = onHoldsToCheck.filter(isPresent);
    if (onHolds.length > 0) {
      const onHoldCollectionIdentifiers = onHoldCollection.map(onHoldItem => getOnHoldIdentifier(onHoldItem)!);
      const onHoldsToAdd = onHolds.filter(onHoldItem => {
        const onHoldIdentifier = getOnHoldIdentifier(onHoldItem);
        if (onHoldIdentifier == null || onHoldCollectionIdentifiers.includes(onHoldIdentifier)) {
          return false;
        }
        onHoldCollectionIdentifiers.push(onHoldIdentifier);
        return true;
      });
      return [...onHoldsToAdd, ...onHoldCollection];
    }
    return onHoldCollection;
  }
}
