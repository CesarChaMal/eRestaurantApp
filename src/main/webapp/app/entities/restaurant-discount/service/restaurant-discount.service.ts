import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRestaurantDiscount, getRestaurantDiscountIdentifier } from '../restaurant-discount.model';

export type EntityResponseType = HttpResponse<IRestaurantDiscount>;
export type EntityArrayResponseType = HttpResponse<IRestaurantDiscount[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantDiscountService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurant-discounts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/restaurant-discounts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(restaurantDiscount: IRestaurantDiscount): Observable<EntityResponseType> {
    return this.http.post<IRestaurantDiscount>(this.resourceUrl, restaurantDiscount, { observe: 'response' });
  }

  update(restaurantDiscount: IRestaurantDiscount): Observable<EntityResponseType> {
    return this.http.put<IRestaurantDiscount>(
      `${this.resourceUrl}/${getRestaurantDiscountIdentifier(restaurantDiscount) as string}`,
      restaurantDiscount,
      { observe: 'response' }
    );
  }

  partialUpdate(restaurantDiscount: IRestaurantDiscount): Observable<EntityResponseType> {
    return this.http.patch<IRestaurantDiscount>(
      `${this.resourceUrl}/${getRestaurantDiscountIdentifier(restaurantDiscount) as string}`,
      restaurantDiscount,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRestaurantDiscount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurantDiscount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurantDiscount[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addRestaurantDiscountToCollectionIfMissing(
    restaurantDiscountCollection: IRestaurantDiscount[],
    ...restaurantDiscountsToCheck: (IRestaurantDiscount | null | undefined)[]
  ): IRestaurantDiscount[] {
    const restaurantDiscounts: IRestaurantDiscount[] = restaurantDiscountsToCheck.filter(isPresent);
    if (restaurantDiscounts.length > 0) {
      const restaurantDiscountCollectionIdentifiers = restaurantDiscountCollection.map(
        restaurantDiscountItem => getRestaurantDiscountIdentifier(restaurantDiscountItem)!
      );
      const restaurantDiscountsToAdd = restaurantDiscounts.filter(restaurantDiscountItem => {
        const restaurantDiscountIdentifier = getRestaurantDiscountIdentifier(restaurantDiscountItem);
        if (restaurantDiscountIdentifier == null || restaurantDiscountCollectionIdentifiers.includes(restaurantDiscountIdentifier)) {
          return false;
        }
        restaurantDiscountCollectionIdentifiers.push(restaurantDiscountIdentifier);
        return true;
      });
      return [...restaurantDiscountsToAdd, ...restaurantDiscountCollection];
    }
    return restaurantDiscountCollection;
  }
}
