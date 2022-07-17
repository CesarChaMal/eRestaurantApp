import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IAppDiscount, getAppDiscountIdentifier } from '../app-discount.model';

export type EntityResponseType = HttpResponse<IAppDiscount>;
export type EntityArrayResponseType = HttpResponse<IAppDiscount[]>;

@Injectable({ providedIn: 'root' })
export class AppDiscountService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/app-discounts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/app-discounts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appDiscount: IAppDiscount): Observable<EntityResponseType> {
    return this.http.post<IAppDiscount>(this.resourceUrl, appDiscount, { observe: 'response' });
  }

  update(appDiscount: IAppDiscount): Observable<EntityResponseType> {
    return this.http.put<IAppDiscount>(`${this.resourceUrl}/${getAppDiscountIdentifier(appDiscount) as string}`, appDiscount, {
      observe: 'response',
    });
  }

  partialUpdate(appDiscount: IAppDiscount): Observable<EntityResponseType> {
    return this.http.patch<IAppDiscount>(`${this.resourceUrl}/${getAppDiscountIdentifier(appDiscount) as string}`, appDiscount, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAppDiscount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppDiscount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppDiscount[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAppDiscountToCollectionIfMissing(
    appDiscountCollection: IAppDiscount[],
    ...appDiscountsToCheck: (IAppDiscount | null | undefined)[]
  ): IAppDiscount[] {
    const appDiscounts: IAppDiscount[] = appDiscountsToCheck.filter(isPresent);
    if (appDiscounts.length > 0) {
      const appDiscountCollectionIdentifiers = appDiscountCollection.map(appDiscountItem => getAppDiscountIdentifier(appDiscountItem)!);
      const appDiscountsToAdd = appDiscounts.filter(appDiscountItem => {
        const appDiscountIdentifier = getAppDiscountIdentifier(appDiscountItem);
        if (appDiscountIdentifier == null || appDiscountCollectionIdentifiers.includes(appDiscountIdentifier)) {
          return false;
        }
        appDiscountCollectionIdentifiers.push(appDiscountIdentifier);
        return true;
      });
      return [...appDiscountsToAdd, ...appDiscountCollection];
    }
    return appDiscountCollection;
  }
}
