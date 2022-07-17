import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IClose, getCloseIdentifier } from '../close.model';

export type EntityResponseType = HttpResponse<IClose>;
export type EntityArrayResponseType = HttpResponse<IClose[]>;

@Injectable({ providedIn: 'root' })
export class CloseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/closes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/closes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(close: IClose): Observable<EntityResponseType> {
    return this.http.post<IClose>(this.resourceUrl, close, { observe: 'response' });
  }

  update(close: IClose): Observable<EntityResponseType> {
    return this.http.put<IClose>(`${this.resourceUrl}/${getCloseIdentifier(close) as string}`, close, { observe: 'response' });
  }

  partialUpdate(close: IClose): Observable<EntityResponseType> {
    return this.http.patch<IClose>(`${this.resourceUrl}/${getCloseIdentifier(close) as string}`, close, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IClose>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClose[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClose[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCloseToCollectionIfMissing(closeCollection: IClose[], ...closesToCheck: (IClose | null | undefined)[]): IClose[] {
    const closes: IClose[] = closesToCheck.filter(isPresent);
    if (closes.length > 0) {
      const closeCollectionIdentifiers = closeCollection.map(closeItem => getCloseIdentifier(closeItem)!);
      const closesToAdd = closes.filter(closeItem => {
        const closeIdentifier = getCloseIdentifier(closeItem);
        if (closeIdentifier == null || closeCollectionIdentifiers.includes(closeIdentifier)) {
          return false;
        }
        closeCollectionIdentifiers.push(closeIdentifier);
        return true;
      });
      return [...closesToAdd, ...closeCollection];
    }
    return closeCollection;
  }
}
