import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IState, getStateIdentifier } from '../state.model';

export type EntityResponseType = HttpResponse<IState>;
export type EntityArrayResponseType = HttpResponse<IState[]>;

@Injectable({ providedIn: 'root' })
export class StateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/states');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/states');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(state: IState): Observable<EntityResponseType> {
    return this.http.post<IState>(this.resourceUrl, state, { observe: 'response' });
  }

  update(state: IState): Observable<EntityResponseType> {
    return this.http.put<IState>(`${this.resourceUrl}/${getStateIdentifier(state) as string}`, state, { observe: 'response' });
  }

  partialUpdate(state: IState): Observable<EntityResponseType> {
    return this.http.patch<IState>(`${this.resourceUrl}/${getStateIdentifier(state) as string}`, state, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IState>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IState[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IState[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addStateToCollectionIfMissing(stateCollection: IState[], ...statesToCheck: (IState | null | undefined)[]): IState[] {
    const states: IState[] = statesToCheck.filter(isPresent);
    if (states.length > 0) {
      const stateCollectionIdentifiers = stateCollection.map(stateItem => getStateIdentifier(stateItem)!);
      const statesToAdd = states.filter(stateItem => {
        const stateIdentifier = getStateIdentifier(stateItem);
        if (stateIdentifier == null || stateCollectionIdentifiers.includes(stateIdentifier)) {
          return false;
        }
        stateCollectionIdentifiers.push(stateIdentifier);
        return true;
      });
      return [...statesToAdd, ...stateCollection];
    }
    return stateCollection;
  }
}
