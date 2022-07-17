import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRestaurantAd, getRestaurantAdIdentifier } from '../restaurant-ad.model';

export type EntityResponseType = HttpResponse<IRestaurantAd>;
export type EntityArrayResponseType = HttpResponse<IRestaurantAd[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantAdService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurant-ads');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/restaurant-ads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(restaurantAd: IRestaurantAd): Observable<EntityResponseType> {
    return this.http.post<IRestaurantAd>(this.resourceUrl, restaurantAd, { observe: 'response' });
  }

  update(restaurantAd: IRestaurantAd): Observable<EntityResponseType> {
    return this.http.put<IRestaurantAd>(`${this.resourceUrl}/${getRestaurantAdIdentifier(restaurantAd) as string}`, restaurantAd, {
      observe: 'response',
    });
  }

  partialUpdate(restaurantAd: IRestaurantAd): Observable<EntityResponseType> {
    return this.http.patch<IRestaurantAd>(`${this.resourceUrl}/${getRestaurantAdIdentifier(restaurantAd) as string}`, restaurantAd, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IRestaurantAd>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurantAd[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurantAd[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addRestaurantAdToCollectionIfMissing(
    restaurantAdCollection: IRestaurantAd[],
    ...restaurantAdsToCheck: (IRestaurantAd | null | undefined)[]
  ): IRestaurantAd[] {
    const restaurantAds: IRestaurantAd[] = restaurantAdsToCheck.filter(isPresent);
    if (restaurantAds.length > 0) {
      const restaurantAdCollectionIdentifiers = restaurantAdCollection.map(
        restaurantAdItem => getRestaurantAdIdentifier(restaurantAdItem)!
      );
      const restaurantAdsToAdd = restaurantAds.filter(restaurantAdItem => {
        const restaurantAdIdentifier = getRestaurantAdIdentifier(restaurantAdItem);
        if (restaurantAdIdentifier == null || restaurantAdCollectionIdentifiers.includes(restaurantAdIdentifier)) {
          return false;
        }
        restaurantAdCollectionIdentifiers.push(restaurantAdIdentifier);
        return true;
      });
      return [...restaurantAdsToAdd, ...restaurantAdCollection];
    }
    return restaurantAdCollection;
  }
}
