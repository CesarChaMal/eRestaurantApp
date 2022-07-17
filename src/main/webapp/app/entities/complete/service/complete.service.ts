import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IComplete, getCompleteIdentifier } from '../complete.model';

export type EntityResponseType = HttpResponse<IComplete>;
export type EntityArrayResponseType = HttpResponse<IComplete[]>;

@Injectable({ providedIn: 'root' })
export class CompleteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/completes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/completes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(complete: IComplete): Observable<EntityResponseType> {
    return this.http.post<IComplete>(this.resourceUrl, complete, { observe: 'response' });
  }

  update(complete: IComplete): Observable<EntityResponseType> {
    return this.http.put<IComplete>(`${this.resourceUrl}/${getCompleteIdentifier(complete) as string}`, complete, { observe: 'response' });
  }

  partialUpdate(complete: IComplete): Observable<EntityResponseType> {
    return this.http.patch<IComplete>(`${this.resourceUrl}/${getCompleteIdentifier(complete) as string}`, complete, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IComplete>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IComplete[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IComplete[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCompleteToCollectionIfMissing(completeCollection: IComplete[], ...completesToCheck: (IComplete | null | undefined)[]): IComplete[] {
    const completes: IComplete[] = completesToCheck.filter(isPresent);
    if (completes.length > 0) {
      const completeCollectionIdentifiers = completeCollection.map(completeItem => getCompleteIdentifier(completeItem)!);
      const completesToAdd = completes.filter(completeItem => {
        const completeIdentifier = getCompleteIdentifier(completeItem);
        if (completeIdentifier == null || completeCollectionIdentifiers.includes(completeIdentifier)) {
          return false;
        }
        completeCollectionIdentifiers.push(completeIdentifier);
        return true;
      });
      return [...completesToAdd, ...completeCollection];
    }
    return completeCollection;
  }
}
