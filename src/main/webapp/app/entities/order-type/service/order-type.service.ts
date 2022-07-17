import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOrderType, getOrderTypeIdentifier } from '../order-type.model';

export type EntityResponseType = HttpResponse<IOrderType>;
export type EntityArrayResponseType = HttpResponse<IOrderType[]>;

@Injectable({ providedIn: 'root' })
export class OrderTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/order-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderType: IOrderType): Observable<EntityResponseType> {
    return this.http.post<IOrderType>(this.resourceUrl, orderType, { observe: 'response' });
  }

  update(orderType: IOrderType): Observable<EntityResponseType> {
    return this.http.put<IOrderType>(`${this.resourceUrl}/${getOrderTypeIdentifier(orderType) as string}`, orderType, {
      observe: 'response',
    });
  }

  partialUpdate(orderType: IOrderType): Observable<EntityResponseType> {
    return this.http.patch<IOrderType>(`${this.resourceUrl}/${getOrderTypeIdentifier(orderType) as string}`, orderType, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IOrderType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderType[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOrderTypeToCollectionIfMissing(
    orderTypeCollection: IOrderType[],
    ...orderTypesToCheck: (IOrderType | null | undefined)[]
  ): IOrderType[] {
    const orderTypes: IOrderType[] = orderTypesToCheck.filter(isPresent);
    if (orderTypes.length > 0) {
      const orderTypeCollectionIdentifiers = orderTypeCollection.map(orderTypeItem => getOrderTypeIdentifier(orderTypeItem)!);
      const orderTypesToAdd = orderTypes.filter(orderTypeItem => {
        const orderTypeIdentifier = getOrderTypeIdentifier(orderTypeItem);
        if (orderTypeIdentifier == null || orderTypeCollectionIdentifiers.includes(orderTypeIdentifier)) {
          return false;
        }
        orderTypeCollectionIdentifiers.push(orderTypeIdentifier);
        return true;
      });
      return [...orderTypesToAdd, ...orderTypeCollection];
    }
    return orderTypeCollection;
  }
}
