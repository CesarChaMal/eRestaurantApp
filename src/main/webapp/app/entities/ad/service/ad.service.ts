import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IAd, getAdIdentifier } from '../ad.model';

export type EntityResponseType = HttpResponse<IAd>;
export type EntityArrayResponseType = HttpResponse<IAd[]>;

@Injectable({ providedIn: 'root' })
export class AdService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ads');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/ads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ad: IAd): Observable<EntityResponseType> {
    return this.http.post<IAd>(this.resourceUrl, ad, { observe: 'response' });
  }

  update(ad: IAd): Observable<EntityResponseType> {
    return this.http.put<IAd>(`${this.resourceUrl}/${getAdIdentifier(ad) as string}`, ad, { observe: 'response' });
  }

  partialUpdate(ad: IAd): Observable<EntityResponseType> {
    return this.http.patch<IAd>(`${this.resourceUrl}/${getAdIdentifier(ad) as string}`, ad, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAd>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAd[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAd[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAdToCollectionIfMissing(adCollection: IAd[], ...adsToCheck: (IAd | null | undefined)[]): IAd[] {
    const ads: IAd[] = adsToCheck.filter(isPresent);
    if (ads.length > 0) {
      const adCollectionIdentifiers = adCollection.map(adItem => getAdIdentifier(adItem)!);
      const adsToAdd = ads.filter(adItem => {
        const adIdentifier = getAdIdentifier(adItem);
        if (adIdentifier == null || adCollectionIdentifiers.includes(adIdentifier)) {
          return false;
        }
        adCollectionIdentifiers.push(adIdentifier);
        return true;
      });
      return [...adsToAdd, ...adCollection];
    }
    return adCollection;
  }
}
