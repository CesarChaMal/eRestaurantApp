import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICancel, getCancelIdentifier } from '../cancel.model';

export type EntityResponseType = HttpResponse<ICancel>;
export type EntityArrayResponseType = HttpResponse<ICancel[]>;

@Injectable({ providedIn: 'root' })
export class CancelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cancels');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cancels');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cancel: ICancel): Observable<EntityResponseType> {
    return this.http.post<ICancel>(this.resourceUrl, cancel, { observe: 'response' });
  }

  update(cancel: ICancel): Observable<EntityResponseType> {
    return this.http.put<ICancel>(`${this.resourceUrl}/${getCancelIdentifier(cancel) as string}`, cancel, { observe: 'response' });
  }

  partialUpdate(cancel: ICancel): Observable<EntityResponseType> {
    return this.http.patch<ICancel>(`${this.resourceUrl}/${getCancelIdentifier(cancel) as string}`, cancel, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICancel>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICancel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICancel[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCancelToCollectionIfMissing(cancelCollection: ICancel[], ...cancelsToCheck: (ICancel | null | undefined)[]): ICancel[] {
    const cancels: ICancel[] = cancelsToCheck.filter(isPresent);
    if (cancels.length > 0) {
      const cancelCollectionIdentifiers = cancelCollection.map(cancelItem => getCancelIdentifier(cancelItem)!);
      const cancelsToAdd = cancels.filter(cancelItem => {
        const cancelIdentifier = getCancelIdentifier(cancelItem);
        if (cancelIdentifier == null || cancelCollectionIdentifiers.includes(cancelIdentifier)) {
          return false;
        }
        cancelCollectionIdentifiers.push(cancelIdentifier);
        return true;
      });
      return [...cancelsToAdd, ...cancelCollection];
    }
    return cancelCollection;
  }
}
