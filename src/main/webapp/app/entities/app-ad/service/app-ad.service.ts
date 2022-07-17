import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IAppAd, getAppAdIdentifier } from '../app-ad.model';

export type EntityResponseType = HttpResponse<IAppAd>;
export type EntityArrayResponseType = HttpResponse<IAppAd[]>;

@Injectable({ providedIn: 'root' })
export class AppAdService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/app-ads');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/app-ads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appAd: IAppAd): Observable<EntityResponseType> {
    return this.http.post<IAppAd>(this.resourceUrl, appAd, { observe: 'response' });
  }

  update(appAd: IAppAd): Observable<EntityResponseType> {
    return this.http.put<IAppAd>(`${this.resourceUrl}/${getAppAdIdentifier(appAd) as string}`, appAd, { observe: 'response' });
  }

  partialUpdate(appAd: IAppAd): Observable<EntityResponseType> {
    return this.http.patch<IAppAd>(`${this.resourceUrl}/${getAppAdIdentifier(appAd) as string}`, appAd, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAppAd>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppAd[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppAd[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAppAdToCollectionIfMissing(appAdCollection: IAppAd[], ...appAdsToCheck: (IAppAd | null | undefined)[]): IAppAd[] {
    const appAds: IAppAd[] = appAdsToCheck.filter(isPresent);
    if (appAds.length > 0) {
      const appAdCollectionIdentifiers = appAdCollection.map(appAdItem => getAppAdIdentifier(appAdItem)!);
      const appAdsToAdd = appAds.filter(appAdItem => {
        const appAdIdentifier = getAppAdIdentifier(appAdItem);
        if (appAdIdentifier == null || appAdCollectionIdentifiers.includes(appAdIdentifier)) {
          return false;
        }
        appAdCollectionIdentifiers.push(appAdIdentifier);
        return true;
      });
      return [...appAdsToAdd, ...appAdCollection];
    }
    return appAdCollection;
  }
}
