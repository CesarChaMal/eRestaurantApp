import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { INewOrder, getNewOrderIdentifier } from '../new-order.model';

export type EntityResponseType = HttpResponse<INewOrder>;
export type EntityArrayResponseType = HttpResponse<INewOrder[]>;

@Injectable({ providedIn: 'root' })
export class NewOrderService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/new-orders');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/new-orders');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(newOrder: INewOrder): Observable<EntityResponseType> {
    return this.http.post<INewOrder>(this.resourceUrl, newOrder, { observe: 'response' });
  }

  update(newOrder: INewOrder): Observable<EntityResponseType> {
    return this.http.put<INewOrder>(`${this.resourceUrl}/${getNewOrderIdentifier(newOrder) as string}`, newOrder, { observe: 'response' });
  }

  partialUpdate(newOrder: INewOrder): Observable<EntityResponseType> {
    return this.http.patch<INewOrder>(`${this.resourceUrl}/${getNewOrderIdentifier(newOrder) as string}`, newOrder, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<INewOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INewOrder[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INewOrder[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addNewOrderToCollectionIfMissing(newOrderCollection: INewOrder[], ...newOrdersToCheck: (INewOrder | null | undefined)[]): INewOrder[] {
    const newOrders: INewOrder[] = newOrdersToCheck.filter(isPresent);
    if (newOrders.length > 0) {
      const newOrderCollectionIdentifiers = newOrderCollection.map(newOrderItem => getNewOrderIdentifier(newOrderItem)!);
      const newOrdersToAdd = newOrders.filter(newOrderItem => {
        const newOrderIdentifier = getNewOrderIdentifier(newOrderItem);
        if (newOrderIdentifier == null || newOrderCollectionIdentifiers.includes(newOrderIdentifier)) {
          return false;
        }
        newOrderCollectionIdentifiers.push(newOrderIdentifier);
        return true;
      });
      return [...newOrdersToAdd, ...newOrderCollection];
    }
    return newOrderCollection;
  }
}
