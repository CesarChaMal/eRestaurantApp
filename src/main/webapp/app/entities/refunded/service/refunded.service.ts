import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRefunded, getRefundedIdentifier } from '../refunded.model';

export type EntityResponseType = HttpResponse<IRefunded>;
export type EntityArrayResponseType = HttpResponse<IRefunded[]>;

@Injectable({ providedIn: 'root' })
export class RefundedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/refundeds');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/refundeds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(refunded: IRefunded): Observable<EntityResponseType> {
    return this.http.post<IRefunded>(this.resourceUrl, refunded, { observe: 'response' });
  }

  update(refunded: IRefunded): Observable<EntityResponseType> {
    return this.http.put<IRefunded>(`${this.resourceUrl}/${getRefundedIdentifier(refunded) as string}`, refunded, { observe: 'response' });
  }

  partialUpdate(refunded: IRefunded): Observable<EntityResponseType> {
    return this.http.patch<IRefunded>(`${this.resourceUrl}/${getRefundedIdentifier(refunded) as string}`, refunded, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRefunded>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRefunded[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRefunded[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addRefundedToCollectionIfMissing(refundedCollection: IRefunded[], ...refundedsToCheck: (IRefunded | null | undefined)[]): IRefunded[] {
    const refundeds: IRefunded[] = refundedsToCheck.filter(isPresent);
    if (refundeds.length > 0) {
      const refundedCollectionIdentifiers = refundedCollection.map(refundedItem => getRefundedIdentifier(refundedItem)!);
      const refundedsToAdd = refundeds.filter(refundedItem => {
        const refundedIdentifier = getRefundedIdentifier(refundedItem);
        if (refundedIdentifier == null || refundedCollectionIdentifiers.includes(refundedIdentifier)) {
          return false;
        }
        refundedCollectionIdentifiers.push(refundedIdentifier);
        return true;
      });
      return [...refundedsToAdd, ...refundedCollection];
    }
    return refundedCollection;
  }
}
