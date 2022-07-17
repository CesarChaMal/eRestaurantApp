import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRestaurant, getRestaurantIdentifier } from '../restaurant.model';

export type EntityResponseType = HttpResponse<IRestaurant>;
export type EntityArrayResponseType = HttpResponse<IRestaurant[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurants');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/restaurants');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(restaurant: IRestaurant): Observable<EntityResponseType> {
    return this.http.post<IRestaurant>(this.resourceUrl, restaurant, { observe: 'response' });
  }

  update(restaurant: IRestaurant): Observable<EntityResponseType> {
    return this.http.put<IRestaurant>(`${this.resourceUrl}/${getRestaurantIdentifier(restaurant) as string}`, restaurant, {
      observe: 'response',
    });
  }

  partialUpdate(restaurant: IRestaurant): Observable<EntityResponseType> {
    return this.http.patch<IRestaurant>(`${this.resourceUrl}/${getRestaurantIdentifier(restaurant) as string}`, restaurant, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRestaurant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurant[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addRestaurantToCollectionIfMissing(
    restaurantCollection: IRestaurant[],
    ...restaurantsToCheck: (IRestaurant | null | undefined)[]
  ): IRestaurant[] {
    const restaurants: IRestaurant[] = restaurantsToCheck.filter(isPresent);
    if (restaurants.length > 0) {
      const restaurantCollectionIdentifiers = restaurantCollection.map(restaurantItem => getRestaurantIdentifier(restaurantItem)!);
      const restaurantsToAdd = restaurants.filter(restaurantItem => {
        const restaurantIdentifier = getRestaurantIdentifier(restaurantItem);
        if (restaurantIdentifier == null || restaurantCollectionIdentifiers.includes(restaurantIdentifier)) {
          return false;
        }
        restaurantCollectionIdentifiers.push(restaurantIdentifier);
        return true;
      });
      return [...restaurantsToAdd, ...restaurantCollection];
    }
    return restaurantCollection;
  }
}
