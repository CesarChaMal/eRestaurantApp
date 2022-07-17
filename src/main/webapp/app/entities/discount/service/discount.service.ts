import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IDiscount, getDiscountIdentifier } from '../discount.model';

export type EntityResponseType = HttpResponse<IDiscount>;
export type EntityArrayResponseType = HttpResponse<IDiscount[]>;

@Injectable({ providedIn: 'root' })
export class DiscountService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/discounts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/discounts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(discount: IDiscount): Observable<EntityResponseType> {
    return this.http.post<IDiscount>(this.resourceUrl, discount, { observe: 'response' });
  }

  update(discount: IDiscount): Observable<EntityResponseType> {
    return this.http.put<IDiscount>(`${this.resourceUrl}/${getDiscountIdentifier(discount) as string}`, discount, { observe: 'response' });
  }

  partialUpdate(discount: IDiscount): Observable<EntityResponseType> {
    return this.http.patch<IDiscount>(`${this.resourceUrl}/${getDiscountIdentifier(discount) as string}`, discount, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IDiscount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiscount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiscount[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addDiscountToCollectionIfMissing(discountCollection: IDiscount[], ...discountsToCheck: (IDiscount | null | undefined)[]): IDiscount[] {
    const discounts: IDiscount[] = discountsToCheck.filter(isPresent);
    if (discounts.length > 0) {
      const discountCollectionIdentifiers = discountCollection.map(discountItem => getDiscountIdentifier(discountItem)!);
      const discountsToAdd = discounts.filter(discountItem => {
        const discountIdentifier = getDiscountIdentifier(discountItem);
        if (discountIdentifier == null || discountCollectionIdentifiers.includes(discountIdentifier)) {
          return false;
        }
        discountCollectionIdentifiers.push(discountIdentifier);
        return true;
      });
      return [...discountsToAdd, ...discountCollection];
    }
    return discountCollection;
  }
}
